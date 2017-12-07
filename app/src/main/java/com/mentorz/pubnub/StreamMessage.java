package com.mentorz.pubnub;



/**
 * Created by Vishal Gaur on 9/2/2016.
 */
public abstract class StreamMessage extends BasePacket {
    public StreamMessage(Long senderID) {
        super(senderID);
    }
}
