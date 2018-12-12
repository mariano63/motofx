package org.ma.motofx.socks;

public class TravellingObj {
    enum Cmd{
        //CLIENT Commands
        CLIENT_INIT,
        //SERVER Commands
        SERVER_INIT;
    }

    private Cmd cmd;
    String p1,p2;

    public Cmd getCmd() {
        return cmd;
    }
}
