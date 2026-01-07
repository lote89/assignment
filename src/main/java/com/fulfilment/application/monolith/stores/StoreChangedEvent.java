package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.stores.Store;

public record StoreChangedEvent(Store store, Operation operation) {}

enum Operation { CREATED, UPDATED, DELETED }
