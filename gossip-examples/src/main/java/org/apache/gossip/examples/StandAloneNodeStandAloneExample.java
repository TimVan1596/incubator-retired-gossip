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
import java.util.Scanner;

import org.apache.gossip.manager.GossipManager;

public class StandAloneNodeStandAloneExample extends BaseStandAloneExample {

  private static boolean WILL_READ = false;

  public static void main(String[] args) throws InterruptedException, IOException {

//    udp://localhost:5400 0 udp://localhost:10000 0
//netstat -aon|findstr "5400"
// tasklist|findstr "2720"

    String[] paraURL1 = {"udp://localhost:5400","0","udp://localhost:5400","0"};
    String[] paraURL2 = {"udp://localhost:5401","1","udp://localhost:5400","0"};
    String[] paraURL3 = {"udp://localhost:5402","2","udp://localhost:5400","0"};
    String[] paraURL4 = {"udp://localhost:5403","3","udp://localhost:5400","0"};

    System.out.println("StandAloneNode - 请输入启动节点列表编号");
    Scanner scanner = new Scanner(System.in);
    String[] paraURL = null;
    int optionNum = scanner.nextInt();
    switch (optionNum){
      case 1:{
        paraURL = paraURL1;
        break;
      }
      case 2:{
        paraURL = paraURL2;
        break;
      }
      case 3:{
        paraURL = paraURL3;
        break;
      }
      default:{
         paraURL = paraURL4;

      }
    }
    System.out.println(String.format("%d号节点开始运行 -- port = %s , id = %s"
            , optionNum,paraURL[0].substring(paraURL[0].length()-4,paraURL[0].length()),paraURL[1]));

    StandAloneNodeStandAloneExample example = new StandAloneNodeStandAloneExample(paraURL);
    example.exec(WILL_READ);
  }

  StandAloneNodeStandAloneExample(String[] paraURL) {
    paraURL = super.checkArgsForClearFlag(paraURL);
    super.initGossipManager(paraURL);
  }

  @Override
  void printValues(GossipManager gossipService) {
  }

}
