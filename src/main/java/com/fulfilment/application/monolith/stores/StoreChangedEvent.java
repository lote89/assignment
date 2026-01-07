package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.stores.domain.Store;

public record StoreChangedEvent(Store store, Operation operation) {}

enum Operation { CREATED, UPDATED, DELETED }
