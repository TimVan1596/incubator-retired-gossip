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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.gossip.GossipSettings;
import org.apache.gossip.LocalMember;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;


/**
 * StandAloneExampleBase - 抽象类，作为实际运行节点的基础
 */
abstract class BaseStandAloneExample {
    /**
     * lastInput = 节点最后一次接收的值
     */
    private String lastInput = "{none}";
    private boolean clearTerminalScreen = true;
    private GossipManager gossipService = null;

    /**
     * 打印存储数据
     */
    abstract void printValues(GossipManager gossipService);

    /** 处理读取的字符串 */
    boolean processReadLoopInput(String line) {
        return true;
    }

    /**
     * 重要！ 主程序运行函数
     */
    void exec(boolean willRead) throws IOException {
        //初始化 gossipService
        gossipService.init();
        //开始监视器循环（每隔2s打印数据）
        startMonitorLoop(gossipService);

        //允许循环读取数据
        if (willRead) {
            startBlockingReadLoop();
        }
    }

    /**
     * 在args中查找-s。如果存在，则阻止写入结果的终端清屏：如有必要，将args转换为位置参数
     */
    String[] checkArgsForClearFlag(String[] args) {
        int pos = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                clearTerminalScreen = false;
            } else {
//                存在在-s标志时，将args向下移一格
//                最终在args的最后一个位置出现重复项，但这没关系，因为它将被忽略
                args[pos++] = args[i];
            }
        }
        return args;
    }

    private void optionallyClearTerminal() {
        if (clearTerminalScreen) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private void setLastInput(String input, boolean valid) {
        lastInput = input;
        if (!valid) {
            lastInput += " (invalid)";
        }
    }

    String getLastInput() {
        return lastInput;
    }

    /**
     * 监视器循环
     */
    private void startMonitorLoop(GossipManager gossipService) {
        new Thread(() -> {
            while (true) {
                Date date = new Date();
                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sim.format(date);
                System.out.println("\n\n  --------------" + time + "：");
                optionallyClearTerminal();
                printLiveMembers(gossipService);
                printDeadMambers(gossipService);
                printValues(gossipService);
                try {
                    Thread.sleep(2000);
                } catch (Exception ignore) {
                }
            }
        }).start();
    }

    private void printLiveMembers(GossipManager gossipService) {
        List<LocalMember> members = gossipService.getLiveMembers();
        if (members.isEmpty()) {
            System.out.println("Live: (none)");
            return;
        }
        System.out.println("Live: " + members.get(0));
        for (int i = 1; i < members.size(); i++) {
            System.out.println("    : " + members.get(i));
        }
    }

    private void printDeadMambers(GossipManager gossipService) {
        List<LocalMember> members = gossipService.getDeadMembers();
        if (members.isEmpty()) {
            System.out.println("Dead: (none)");
            return;
        }
        System.out.println("Dead: " + members.get(0));
        for (int i = 1; i < members.size(); i++) {
            System.out.println("    : " + members.get(i));
        }
    }

    /**
     * 可以循环读取输入流
     * */
    private void startBlockingReadLoop() throws IOException {
        String line;
        try (BufferedReader br = new
                BufferedReader(new InputStreamReader(System.in))) {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                //处理读取的字符串
                boolean valid = processReadLoopInput(line);
                setLastInput(line, valid);
            }
        }
    }

    /**
     * 重要！ 初始化GossipManager参数
     */
    void initGossipManager(String[] args) {
        GossipSettings settings = new GossipSettings();
        settings.setWindowSize(1000);
        settings.setGossipInterval(1000);
        GossipManager gossipService = GossipManagerBuilder.newBuilder()
                .cluster("mycluster")
                .uri(URI.create(args[0])).id(args[1])
                .gossipMembers(Collections
                        .singletonList(new RemoteMember("mycluster", URI.create(args[2]), args[3])))
                .gossipSettings(settings).build();

        setGossipService(gossipService);
    }

    void setGossipService(GossipManager gossipService) {
        this.gossipService = gossipService;
    }

    GossipManager getGossipManager() {
        return this.gossipService;
    }

}
