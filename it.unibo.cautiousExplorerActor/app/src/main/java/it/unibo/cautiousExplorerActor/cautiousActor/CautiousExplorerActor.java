package it.unibo.cautiousExplorerActor.cautiousActor;

import it.unibo.cautiousExplorerActor.robotWithActor.RobotMovesInfo;
import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

import javax.swing.plaf.synth.SynthTextAreaUI;

public class CautiousExplorerActor extends ActorBasicJava {
    final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 350}";
    final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 350}";
    final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";
    private IssWsHttpJavaSupport support;
    private enum State {Start,EsplorazioneAntiOraria,EsplorazioneOraria,Ostacolo,TornaInDen,End}
    private State currentState = State.Start;
    private RobotMovesInfo mappaAmbiente = new RobotMovesInfo(true);

    private boolean sensoAntiOrario=true;
    private int raggioEsplorazione =0;
    private int distanzaDallaDen = 0;
    private int numeroRotazioni=0;
    private String elencoMosse="";
    private String elencoMosseInvertite="";
    private boolean finito=false;

    public CautiousExplorerActor(String name,IssWsHttpJavaSupport support){
        super(name);
        this.support=support;
    }



    protected void fsm(String move,String endmove) {

        switch (currentState) {
            case Start:{
                     if(sensoAntiOrario){
                         System.out.println("case=Start && condition=sensoAntiOrario");
                         elencoMosse="";
                         raggioEsplorazione++;
                         numeroRotazioni=0;
                         currentState=State.EsplorazioneAntiOraria;
                         elencoMosse+="w";
                         mappaAmbiente.updateMovesRep("w");
                         mappaAmbiente.showRobotMovesRepresentation();
                         distanzaDallaDen++;
                         doStep();
                     }else if(!sensoAntiOrario){
                         System.out.println("case=Start && condition=sensoAntiOrario");
                         elencoMosse="w";
                         mappaAmbiente.updateMovesRep("w");
                         mappaAmbiente.showRobotMovesRepresentation();
                         currentState=State.EsplorazioneOraria;
                         distanzaDallaDen++;
                         doStep();
                     }
                break;
            }//Start
            case EsplorazioneAntiOraria:{
                if(move.equals("moveForward") && endmove.equals("false") && numeroRotazioni==0){
                    System.out.println("case=EsplorazioneAntiOraria && condition=move.equals(\"moveForward\") && endmove.equals(\"false\") && numeroRotazioni==0");
                    turnLeft();
                    mappaAmbiente.updateMovesRep("l");
                    mappaAmbiente.showRobotMovesRepresentation();
                    currentState= State.Ostacolo;
                    finito=true;
                }else if(move.equals("moveForward") && endmove.equals("false") && numeroRotazioni!=0){
                    System.out.println("case=EsplorazioneAntiOraria && condition=move.equals(\"moveForward\") && endmove.equals(\"false\") && numeroRotazioni!=0");
                    turnLeft();
                    currentState = State.Ostacolo;
                    mappaAmbiente.updateMovesRep("l");
                    mappaAmbiente.showRobotMovesRepresentation();
                }else if(distanzaDallaDen==raggioEsplorazione && numeroRotazioni==3){
                    System.out.println("case=EsplorazioneAntiOraria && condition=distanzaDallaDen==raggioEsplorazione && numeroRotazioni==3");
                  //  elencoMosse+="l";
                  //  mappaAmbiente.updateMovesRep("l");
                    mappaAmbiente.showRobotMovesRepresentation();
                    distanzaDallaDen=0;
                    currentState= State.Start;
                    turnLeft();
                }else if(distanzaDallaDen==raggioEsplorazione){
                    System.out.println("case=EsplorazioneAntiOraria && condition=distanzaDallaDen==raggioEsplorazione");
                    elencoMosse+="l";
                    mappaAmbiente.updateMovesRep("l");
                    mappaAmbiente.showRobotMovesRepresentation();
                    distanzaDallaDen=0;
                    numeroRotazioni++;
                    turnLeft();
                }else if(move.equals("turnLeft") && endmove.equals("true")){
                    System.out.println("case=EsplorazioneAntiOraria && condition=move.equals(\"turnLeft\") && endmove.equals(\"true\")");
                    elencoMosse+="w";
                    mappaAmbiente.updateMovesRep("w");
                    mappaAmbiente.showRobotMovesRepresentation();
                    distanzaDallaDen++;
                    doStep();

                }else if(move.equals("moveForward") && endmove.equals("true") && distanzaDallaDen<raggioEsplorazione){
                    System.out.println("case=EsplorazioneAntiOraria && condition=move.equals(\"moveForward\") && endmove.equals(\"true\") && distanzaDallaDen<raggioEsplorazione");
                    elencoMosse+="w";
                    mappaAmbiente.updateMovesRep("w");
                    mappaAmbiente.showRobotMovesRepresentation();
                    distanzaDallaDen++;
                    doStep();
                }
                break;
            }//EsplorazioneAntioraria
            case Ostacolo:{
                System.out.println("case=Ostacolo && mosse="+elencoMosse);
                currentState=State.TornaInDen;
                elencoMosse= new StringBuilder(elencoMosse).reverse().toString();
                turnLeft();
                break;
            }//Ostacolo
            case TornaInDen:{
               if(!elencoMosse.isEmpty()){
                   System.out.println("case=TornaInDen && condition=!elencoMosse.isEmpty()");
                   char mossa = elencoMosse.charAt(0);
                   elencoMosse = elencoMosse.substring(1,elencoMosse.length());
                   switch (mossa){
                       case 'w':{
                           doStep();
                           break;
                       }
                       case 'l':{
                           turnRight();
                           break;
                       }
                       case 'r':{
                           turnLeft();
                           break;
                       }
                   }
               }else if(!sensoAntiOrario){
                   System.out.println("case=TornaInDen && condition=!sensoAntiOrario");
                   sensoAntiOrario=!sensoAntiOrario;
                   distanzaDallaDen=0;
                   elencoMosse="";
                   turnLeft();
                   mappaAmbiente.showRobotMovesRepresentation();
               }else if(sensoAntiOrario){
                   System.out.println("case=TornaInDen && condition=sensoAntiOrario");
                   sensoAntiOrario=!sensoAntiOrario;
                   distanzaDallaDen=0;
                   elencoMosse="";
                   turnRight();
                   mappaAmbiente.showRobotMovesRepresentation();
               }
               break;
            }//tornaInDen
            case End:{
                System.out.println("finito esplorazione");
                mappaAmbiente.showRobotMovesRepresentation();
                turnLeft();
                break;
            }
        }
    }

    @Override
    protected void handleInput(String msg ) {     //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);
        if(msg.equals("startApp")) fsm("msg","");
       else  msgDriven( new JSONObject(msg) );
    }

    protected void msgDriven( JSONObject infoJson){

        if( infoJson.has("endmove") )        fsm(infoJson.getString("move"), infoJson.getString("endmove"));
    //    else if( infoJson.has("sonarName") ) handleSonar(infoJson);
      //    else if( infoJson.has("collision") ) handleCollision(infoJson);
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

    //------------------------------------------------
    protected void doStep(){
        support.forward( forwardMsg);
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

}
