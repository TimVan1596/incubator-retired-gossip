/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gossip.examples;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.manager.DatacenterRackAwareActiveGossiper;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;

public class StandAloneDatacenterAndRackStandAloneExample extends BaseStandAloneExample {

  public static void main(String[] args) throws  IOException {

//    //udp://localhost:5400 0 udp://localhost:10000 0
//    //netstat -aon|findstr "5400"
//    //tasklist|findstr "2720"
//
//    String[] paraURL1 = {"udp://localhost:5400","0"
//            ,"udp://localhost:5400","0","1","2"};
//    String[] paraURL2 = {"udp://localhost:5402","2"
//            ,"udp://localhost:5401","1","1","2"};
//    String[] paraURL3 = {"udp://localhost:5403","3"
//            ,"udp://localhost:5401","1","2","1"};
//    String[] paraURL4 = {"udp://localhost:5404","4"
//            ,"udp://localhost:5401","1","4","1"};
//
//    System.out.println("StandAloneDatacenterAndRack - 请输入启动节点列表编号");
//    Scanner scanner = new Scanner(System.in);
//    String[] paraURL = null;
//    int optionNum = scanner.nextInt();
//    switch (optionNum){
//      case 1:{
//        paraURL = paraURL1;
//        break;
//      }
//      case 2:{
//        paraURL = paraURL2;
//        break;
//      }
//      case 3:{
//        paraURL = paraURL3;
//        break;
//      }
//      default:{
//        paraURL = paraURL4;
//
//      }
//    }
//    System.out.println(String.format("%d号节点开始运行 -- port = %s , id = %s"
//            , optionNum,paraURL[0].substring(paraURL[0].length()-4,paraURL[0].length()),paraURL[1]));
//    System.out.println("当前数据中心ID="+paraURL[4]+"，机架ID="+paraURL[5]);
//
//
//    StandAloneDatacenterAndRack example = new StandAloneDatacenterAndRack(paraURL);
    StandAloneDatacenterAndRackStandAloneExample example = new StandAloneDatacenterAndRackStandAloneExample(args);

    for (String inst : args) {
      System.out.println(inst);
    }
    boolean willRead = true;
    example.exec(willRead);
  }

  StandAloneDatacenterAndRackStandAloneExample(String[] param) {
    param = super.checkArgsForClearFlag(param);
    initGossipManager(param);
  }

  @Override
  void initGossipManager(String[] args) {
    GossipSettings s = new GossipSettings();
    s.setWindowSize(1000);
    s.setGossipInterval(100);
    s.setActiveGossipClass(DatacenterRackAwareActiveGossiper.class.getName());
    Map<String, String> gossipProps = new HashMap<>();
    gossipProps.put("sameRackGossipIntervalMs", "2000");
    gossipProps.put("differentDatacenterGossipIntervalMs", "10000");
    s.setActiveGossipProperties(gossipProps);
    Map<String, String> props = new HashMap<>();
    props.put(DatacenterRackAwareActiveGossiper.DATACENTER, args[4]);
    props.put(DatacenterRackAwareActiveGossiper.RACK, args[5]);
    GossipManager manager = GossipManagerBuilder.newBuilder().cluster("mycluster")
            .uri(URI.create(args[0])).id(args[1]).gossipSettings(s)
            .gossipMembers(
                    Arrays.asList(new RemoteMember("mycluster", URI.create(args[2]), args[3])))
            .properties(props).build();
    manager.init();
    setGossipService(manager);
  }

  @Override
  void printValues(GossipManager gossipService) {
    return;
  }

}
