package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreSyncListener {
    @Inject LegacyStoreManagerGateway legacyGateway;

    public void onStoreChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreChangedEvent event) {
        switch (event.operation()) {
            case CREATED -> legacyGateway.create(event.store());
            case UPDATED -> legacyGateway.update(event.store());
            case DELETED -> legacyGateway.delete(event.store().id());  // Adjust args
        }
    }
}
