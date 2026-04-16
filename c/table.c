#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#include "memory.h"
#include "object.h"
#include "table.h"
#include "value.h"

#define TABLE_MAX_LOAD 0.75

static uint32_t hashBits(uint64_t bits) {
  return (uint32_t)(bits ^ (bits >> 32));
}

static uint32_t hashValue(Value value) {
  if (IS_BOOL(value)) {
    return AS_BOOL(value) ? 1 : 2;
  }

  if (IS_NIL(value)) {
    return 3;
  }

  if (IS_NUMBER(value)) {
    double num = AS_NUMBER(value);

    // Make +0 and -0 hash the same.
    if (num == 0) return 4;

    uint64_t bits;
    memcpy(&bits, &num, sizeof(double));
    return hashBits(bits);
  }

  if (IS_OBJ(value)) {
    if (IS_STRING(value)) {
      return AS_STRING(value)->hash;
    }

    return hashBits((uintptr_t)AS_OBJ(value));
  }

  return 0;
}

void initTable(Table* table) {
  table->count = 0;
  table->capacity = 0;
  table->entries = NULL;
}

void freeTable(Table* table) {
  FREE_ARRAY(Entry, table->entries, table->capacity);
  initTable(table);
}

static Entry* findEntry(Entry* entries, int capacity, Value key) {
  uint32_t index = hashValue(key) & (capacity - 1);
  Entry* tombstone = NULL;

  for (;;) {
    Entry* entry = &entries[index];

    if (entry->state == ENTRY_EMPTY) {
      return tombstone != NULL ? tombstone : entry;
    } else if (entry->state == ENTRY_TOMBSTONE) {
      if (tombstone == NULL) tombstone = entry;
    } else if (valuesEqual(entry->key, key)) {
      return entry;
    }

    index = (index + 1) & (capacity - 1);
  }
}

bool tableGet(Table* table, Value key, Value* value) {
  if (table->count == 0) return false;

  Entry* entry = findEntry(table->entries, table->capacity, key);
  if (entry->state != ENTRY_OCCUPIED) return false;

  *value = entry->value;
  return true;
}

static void adjustCapacity(Table* table, int capacity) {
  Entry* entries = ALLOCATE(Entry, capacity);
  for (int i = 0; i < capacity; i++) {
    entries[i].state = ENTRY_EMPTY;
    entries[i].key = NIL_VAL;
    entries[i].value = NIL_VAL;
  }

  table->count = 0;

  for (int i = 0; i < table->capacity; i++) {
    Entry* entry = &table->entries[i];
    if (entry->state != ENTRY_OCCUPIED) continue;

    Entry* dest = findEntry(entries, capacity, entry->key);
    dest->state = ENTRY_OCCUPIED;
    dest->key = entry->key;
    dest->value = entry->value;
    table->count++;
  }

  FREE_ARRAY(Entry, table->entries, table->capacity);
  table->entries = entries;
  table->capacity = capacity;
}

bool tableSet(Table* table, Value key, Value value) {
  if (table->count + 1 > table->capacity * TABLE_MAX_LOAD) {
    int capacity = GROW_CAPACITY(table->capacity);
    adjustCapacity(table, capacity);
  }

  Entry* entry = findEntry(table->entries, table->capacity, key);
  bool isNewKey = entry->state != ENTRY_OCCUPIED;

  if (isNewKey && entry->state == ENTRY_EMPTY) {
    table->count++;
  }

  entry->state = ENTRY_OCCUPIED;
  entry->key = key;
  entry->value = value;
  return isNewKey;
}

bool tableDelete(Table* table, Value key) {
  if (table->count == 0) return false;

  Entry* entry = findEntry(table->entries, table->capacity, key);
  if (entry->state != ENTRY_OCCUPIED) return false;

  entry->state = ENTRY_TOMBSTONE;
  entry->key = NIL_VAL;
  entry->value = BOOL_VAL(true);
  return true;
}

void tableAddAll(Table* from, Table* to) {
  for (int i = 0; i < from->capacity; i++) {
    Entry* entry = &from->entries[i];
    if (entry->state == ENTRY_OCCUPIED) {
      tableSet(to, entry->key, entry->value);
    }
  }
}

ObjString* tableFindString(Table* table, const char* chars,
                           int length, uint32_t hash) {
  if (table->count == 0) return NULL;

  uint32_t index = hash & (table->capacity - 1);

  for (;;) {
    Entry* entry = &table->entries[index];

    if (entry->state == ENTRY_EMPTY) {
      return NULL;
    } else if (entry->state == ENTRY_OCCUPIED &&
               IS_STRING(entry->key) &&
               AS_STRING(entry->key)->length == length &&
               AS_STRING(entry->key)->hash == hash &&
               memcmp(AS_STRING(entry->key)->chars, chars, length) == 0) {
      return AS_STRING(entry->key);
    }

    index = (index + 1) & (table->capacity - 1);
  }
}

void tableRemoveWhite(Table* table) {
  for (int i = 0; i < table->capacity; i++) {
    Entry* entry = &table->entries[i];
    if (entry->state == ENTRY_OCCUPIED &&
        IS_OBJ(entry->key) &&
        !AS_OBJ(entry->key)->isMarked) {
      tableDelete(table, entry->key);
    }
  }
}

void markTable(Table* table) {
  for (int i = 0; i < table->capacity; i++) {
    Entry* entry = &table->entries[i];
    if (entry->state == ENTRY_OCCUPIED) {
      markValue(entry->key);
      markValue(entry->value);
    }
  }
}