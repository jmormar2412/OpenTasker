package com.jmormar.opentasker.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class EventosRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventosRemoteViewsFactory(this.getApplicationContext());
    }
}
