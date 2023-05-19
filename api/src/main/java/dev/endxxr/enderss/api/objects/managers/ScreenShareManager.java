package dev.endxxr.enderss.api.objects.managers;

import java.util.UUID;

public interface ScreenShareManager {

    void startScreenShare(UUID staff, UUID suspect);


    void clearPlayer(UUID target);


    void clearPlayer(UUID staff, UUID suspect);

}
