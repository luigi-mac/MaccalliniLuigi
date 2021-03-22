package it.unibo.interaction;

public class AppMsg {
    private static int n = 0;
    private String MSGID;
    private String MSGTYPE;
    private String SENDER;
    private String RECEIVER;
    private String CONTENT;
    private String SEQNUM;

    private enum AppMsgType{ event, dispatch, request, reply, invitation };

    public AppMsg(String MSGID, String MSGTYPE, String SENDER, String RECEIVER, String CONTENT, String SEQNUM){
        this.MSGID    = MSGID;
        this.MSGTYPE  = MSGTYPE;
        this.SENDER   = SENDER;
        this.RECEIVER = RECEIVER;
        this.CONTENT  = CONTENT;
        this.SEQNUM   = SEQNUM;
    }
    //Factory method
    public static AppMsg create(String MSGID, String MSGTYPE, String SENDER, String RECEIVER, String CONTENT){
        //System.out.println("AppMsg create/5 " + CONTENT);
        return new AppMsg( MSGID,  MSGTYPE,  SENDER,  RECEIVER,  CONTENT, ""+n++);
    }

    public static AppMsg create(String m){  //WARNING: CONTENT must be in Prolog syntax
        //System.out.println("AppMsg create m= " + m);
        //m= msg( MSGID,  MSGTYPE,  SENDER,  RECEIVER,  CONTENT, SEQNUM )
        String[] mSplit   = m.split("msg");
        String   mBody[]  = mSplit[1].replace("(","").split(",");
        String   content  = mBody[4].replace(")","");
        return AppMsg.create( mBody[0],  mBody[1],  mBody[2], mBody[3], content );
    }

    public boolean isEvent(){    return MSGTYPE.equals( AppMsgType.event.toString() );    }
    public boolean isDispatch(){ return MSGTYPE.equals( AppMsgType.dispatch.toString() ); }
    public boolean isRequest(){  return MSGTYPE.equals( AppMsgType.request.toString() );  }
    public boolean isReply(){ return MSGTYPE.equals( AppMsgType.reply.toString() );       }

    public String getMsgType(){ return MSGTYPE; }
    public String getMsgid(){ return MSGID; }
    public String getSender(){ return SENDER; }
    public String getReceiver(){ return RECEIVER; }
    public String getContent(){ return CONTENT; }

    public String toString(){
        return "msg(MSGID,MSGTYPE,SENDER,RECEIVER,CONTENT,SEQNUM)"
                .replace("MSGID", MSGID)
                .replace("MSGTYPE", MSGTYPE)
                .replace("SENDER", SENDER)
                .replace("RECEIVER", RECEIVER)
                .replace("CONTENT", CONTENT)
                .replace("SEQNUM", SEQNUM);

    }
}//AppMsg
