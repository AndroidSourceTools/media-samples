/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mediarouter.player;

import android.content.Context;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaRouter.RouteInfo;

/**
 * Abstraction of common playback operations of media items, such as play,
 * seek, etc. Used by PlaybackManager as a backend to handle actual playback
 * of media items.
 */
public abstract class Player {
    protected Callback mCallback;

    public abstract boolean isRemotePlayback();
    public abstract boolean isQueuingSupported();

    public abstract void connect(RouteInfo route);
    public abstract void release();

    // basic operations that are always supported
    public abstract void play(final PlaylistItem item);
    public abstract void seek(final PlaylistItem item);
    public abstract void getStatus(final PlaylistItem item, final boolean update);
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

    // advanced queuing (enqueue & remove) are only supported
    // if isQueuingSupported() returns true
    public abstract void enqueue(final PlaylistItem item);
    public abstract PlaylistItem remove(String iid);

    // route statistics
    public void updateStatistics() {}
    public String getStatistics() { return ""; }

    // presentation display
    public void updatePresentation() {}

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public static Player create(Context context, RouteInfo route) {
        Player player;
        if (route != null && route.supportsControlCategory(
                MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
            player = new RemotePlayer(context);
        } else if (route != null) {
            player = new LocalPlayer.SurfaceViewPlayer(context);
        } else {
            player = new LocalPlayer.OverlayPlayer(context);
        }
        player.connect(route);
        return player;
    }

    public interface Callback {
        void onError();
        void onCompletion();
        void onPlaylistChanged();
        void onPlaylistReady();
    }
}
