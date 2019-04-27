import oscP5.*;
import netP5.*; 
// ref: http://www.sojamo.de/libraries/oscP5
class SoundControl {
  OscP5 oscP5;
  NetAddress myRemoteLocation;


  void setupOSC() {
    /* start oscP5, listening for incoming messages at port 12000 */
    oscP5 = new OscP5(this, 12000);
    /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
     * an ip address and a port number. myRemoteLocation is used as parameter in
     * oscP5.send() when sending osc packets to another computer, device, 
     * application. usage see below. for testing purposes the listening port
     * and the port of the remote location address are the same, hence you will
     * send messages back to this sketch.
     */
    myRemoteLocation = new NetAddress("127.0.0.1", 12000);
  }

  void oscOrder(int order) {
    OscMessage myMessage = new OscMessage("/Order");
    myMessage.add(order); /* add an int to the osc message */

    oscP5.send(myMessage, myRemoteLocation);
  }

  void dispose() {
    OscMessage myMessage = new OscMessage("/Order");
    myMessage.add(0); /* add an int to the osc message */

    oscP5.send(myMessage, myRemoteLocation);
  }

  void updateHarmsOSC(List<Float> harmonics) {
    OscMessage myMessage = new OscMessage("/Harms");
    for (int i = 1; i < harmonics.size(); i++) {
      myMessage.add((float)harmonics.get(i)); /* add float to the osc message */
    }
    oscP5.send(myMessage, myRemoteLocation);
  }
  void sendMessage(OscMessage myMessage) {
    oscP5.send(myMessage, myRemoteLocation);
  }
}
