package it.unibo.robotWithActorJava;

import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

public class BoundaryWalkerActor extends ActorBasicJava {
    final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 350}";
    final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 350}";
    final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";

    private enum State {start, walking, stop, obstacle, end};
    private IssWsHttpJavaSupport support;
    private State curState =  State.start ;
    private int stepNum = 1;
    private int numStop = 0;
    private int numResume = 0;
    private RobotMovesInfo moves = new RobotMovesInfo(true);

    public BoundaryWalkerActor(String name, IssWsHttpJavaSupport support) {
        super(name);
        this.support = support;
    }
/*
//Removed since we want use just the fsm, without any 'external' code
    public void reset(){
        System.out.println("RobotBoundaryLogic | FINAL MAP:"  );
        moves.showRobotMovesRepresentation();
        stepNum        = 1;
        curState       =  State.start;
        moves.getMovesRepresentationAndClean();
        moves.showRobotMovesRepresentation();
    }
*/

    protected void fsm(String move, String endmove){
        System.out.println( myname + " | fsm state=" + curState + " stepNum=" + stepNum + " move=" + move + " endmove=" + endmove);
        switch( curState ) {
            case start: {
                numResume = 0;
                numStop = 0;
                if (move.equals("RESUME")) {
                    moves.showRobotMovesRepresentation();
                    curState = State.walking;
                    numResume++;
                    doStep();
                }
                break;
            }
            case walking: {
                if (move.equals("moveForward") && (endmove.equals("halted") || endmove.equals("true"))) {
                    //curState = State.walk;
                    moves.updateMovesRep("w");
                    doStep();
                 } else if (move.equals("moveForward") && endmove.equals("false")) {
                    curState = State.obstacle;
                    turnLeft();
                } else if (move.equals("STOP")) {
                    curState = State.stop;
                    numStop++;
                    stopJourney();
                } else{ System.out.println("IGNORE answer of turnLeft"); }
                break;
            }//walk

            case stop: {
                if (move.equals("RESUME")) {
                moves.showRobotMovesRepresentation();
                curState = State.walking;
                numResume++;
                doStep();
                }
                break;
            }

            case obstacle :
                if( move.equals("turnLeft") && endmove.equals("true")) {
                    if( stepNum < 4) {
                        stepNum++;
                        moves.updateMovesRep("l");
                        moves.showRobotMovesRepresentation();
                        curState = State.walking;
                        doStep();
                    }else{  //at home again
                        curState = State.end;
                        turnLeft(); //to force state transition
                    }
                } break;

            case end : {
                if( move.equals("turnLeft") ) {
                    System.out.println("BOUNDARY WALK END | numResume=" + numResume + ", numStop=" + numStop);
                    moves.showRobotMovesRepresentation();
                    turnRight();    //to compensate last turnLeft
                }else{
                    //reset();
                    System.out.println("RobotBoundaryLogic | FINAL MAP:"  );
                    moves.showRobotMovesRepresentation();
                    stepNum        = 1;
                    curState       =  State.start;
                    moves.getMovesRepresentationAndClean();
                }
                break;
            }
            default: {
                System.out.println("error - curState = " + curState);
            }
        }
    }


    @Override
    protected void handleInput(String msg ) {     //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);
        if( msg.equals("startApp"))  fsm("","");
        else msgDriven( new JSONObject(msg) );
    }

    protected void msgDriven( JSONObject infoJson){
        if( infoJson.has("endmove") )        fsm(infoJson.getString("move"), infoJson.getString("endmove"));
        else if( infoJson.has("sonarName") ) handleSonar(infoJson);
        else if( infoJson.has("collision") ) handleCollision(infoJson);
        else if( infoJson.has("robotcmd") )  handleRobotCmd(infoJson);
    }

    protected void handleSonar( JSONObject sonarinfo ){
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        //System.out.println("RobotApplication | handleSonar:" + sonarname + " distance=" + distance);
    }
    protected void handleCollision( JSONObject collisioninfo ){
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotApplication | handleCollision move=" + move  );
    }
  
    protected void handleRobotCmd( JSONObject robotCmd ){
        String cmd = (String)  robotCmd.get("robotcmd");
        System.out.println("===================================================="    );
        System.out.println("RobotApplication | handleRobotCmd cmd=" + cmd  );
        System.out.println("===================================================="    );
        fsm(cmd, "");
    }

    //------------------------------------------------
    protected void doStep(){
        support.forward( forwardMsg );
        delay(1000); //to avoid too-rapid movement
    }
    protected void turnLeft(){
        support.forward( turnLeftMsg );
        delay(500); //to avoid too-rapid movement
    }
    protected void turnRight(){
        support.forward( turnRightMsg );
        delay(500); //to avoid too-rapid movement
    }

    protected void stopJourney(){
        support.forward( haltMsg );
        delay(1000);
    }

}
