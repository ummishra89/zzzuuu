package com.mentorz.pubnub;

/**
 * Created by craterzone6 on 2/8/16.
 */
public abstract class BasePacket {
    private Long senderID;

    public BasePacket(Long senderID){
        this.senderID = senderID;
    }

    public Long getSenderID() {
        return senderID;
    }

}
