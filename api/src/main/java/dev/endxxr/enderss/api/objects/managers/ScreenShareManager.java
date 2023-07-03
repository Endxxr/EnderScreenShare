package dev.endxxr.enderss.api.objects.managers;

import java.util.UUID;

public interface ScreenShareManager {

    /**
     *
     * Starts a screenshare session between the staff and the suspect
     * On spigot with the proxy mode enabled, calling this method will only activate the modules specified in the config
     *
     * @param staff the staff member who is starting the screenshare
     * @param suspect the suspect who is being screenshared
     */
    void startScreenShare(UUID staff, UUID suspect);

    /**
     *
     * Stops a screenshare session between the staff and the suspect
     * The target can be both a staff member of a suspect
     *
     * @param target the staff member or the suspect in a session
     */


    void clearPlayer(UUID target);

    /**
     *
     * Stops a screenshare session between the staff and the suspect
     *
     * @param staff the staff member who started the screenshare
     * @param suspect the suspect who is being screenshared
     */

    void clearPlayer(UUID staff, UUID suspect);

}
