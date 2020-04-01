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

import org.apache.gossip.crdt.GrowOnlyCounter;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.model.SharedDataMessage;

public class StandAloneNodeCrdtOrSetStandAloneExample extends BaseStandAloneExample {

    private static final String INDEX_KEY_FOR_SET = "abc";

    private static final String INDEX_KEY_FOR_COUNTER = "def";

    /**
     * 运行流程
     *1、初始化 GossipManager ，根据参数GossipSetting生成GossipService
     *2、初始化并运行GossipService
     *3、运行其他线程：① 监视线程
     *            ②循环读取输入流（命令行）
     * */
    public static void main(String[] args) throws InterruptedException, IOException {

        //    udp://localhost:5400 0 udp://localhost:10000 0
//netstat -aon|findstr "5400"
// tasklist|findstr "2720"

        String[] paraURL1 = {"udp://localhost:5400", "0", "udp://localhost:5400", "0"};
        String[] paraURL2 = {"udp://localhost:5401", "1", "udp://localhost:5400", "0"};
        String[] paraURL3 = {"udp://localhost:5402", "2", "udp://localhost:5400", "0"};
        String[] paraURL4 = {"udp://localhost:5403", "3", "udp://localhost:5400", "0"};

        System.out.println("StandAloneNodeCrdtOrSet - 请输入启动节点列表编号");
        Scanner scanner = new Scanner(System.in);
        String[] paraURL = null;
        int optionNum = scanner.nextInt();
        switch (optionNum) {
            case 1: {
                paraURL = paraURL1;
                break;
            }
            case 2: {
                paraURL = paraURL2;
                break;
            }
            case 3: {
                paraURL = paraURL3;
                break;
            }
            default: {
                paraURL = paraURL4;

            }
        }
        System.out.println(String.format("%d号节点开始运行 -- port = %s , id = %s"
                , optionNum, paraURL[0].substring(paraURL[0].length() - 4, paraURL[0].length()), paraURL[1]));

        StandAloneNodeCrdtOrSetStandAloneExample example = new StandAloneNodeCrdtOrSetStandAloneExample(paraURL);
        boolean willRead = true;
        example.exec(willRead);
    }

    StandAloneNodeCrdtOrSetStandAloneExample(String[] args) {
        args = super.checkArgsForClearFlag(args);
        super.initGossipManager(args);
    }

    @Override
    void printValues(GossipManager gossipService) {
        System.out.println("Last Input: " + getLastInput());
        System.out.println("---------- Or Set " + (gossipService.findCrdt(INDEX_KEY_FOR_SET) == null
                ? "" : gossipService.findCrdt(INDEX_KEY_FOR_SET).value()));
        System.out.println("********** " + gossipService.findCrdt(INDEX_KEY_FOR_SET));
        System.out.println(
                "^^^^^^^^^^ Grow Only Counter" + (gossipService.findCrdt(INDEX_KEY_FOR_COUNTER) == null
                        ? "" : gossipService.findCrdt(INDEX_KEY_FOR_COUNTER).value()));
        System.out.println("$$$$$$$$$$ " + gossipService.findCrdt(INDEX_KEY_FOR_COUNTER));
    }

    /**
     * 覆写父类 - 处理读取的字符串（命令）
     */
    @Override
    boolean processReadLoopInput(String line) {
        //是否是无效命令行
        boolean valid = true;

        //op = 操作符
        // a 是“添加”命令； r 是“删除”命令；g 是“全局增量”命令。
        char op = line.charAt(0);
        String val = line.substring(2);
        if (op == 'a') {
            addData(val, getGossipManager());
        } else if (op == 'r') {
            removeData(val, getGossipManager());
        } else if (op == 'g') {
            //判断是否为非负数
            if (isNonNegativeNumber(val)) {
                globalCounter(val, getGossipManager());
            } else {
                valid = false;
            }
        }
        //TODO:l = listen作用未知
        else if (op == 'l') {
            if ((val.equals(INDEX_KEY_FOR_SET))
                    || (val.equals(INDEX_KEY_FOR_COUNTER))) {
                listen(val, getGossipManager());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    /**
     * 是否为非负数
     */
    private boolean isNonNegativeNumber(String val) {
        long l = 0;
        try {
            l = Long.parseLong(val);
        } catch (Exception e) {
            return false;
        }
        return (l >= 0);
    }

    //TODO:listen作用未知
    private static void listen(String val, GossipManager gossipManager) {
        gossipManager.registerSharedDataSubscriber(
                (key, oldValue, newValue) -> {
                    if (key.equals(val)) {
                        System.out.println(
                                "Event Handler fired for key = '"
                                        + key + "'! " + oldValue + " "
                                        + newValue);
                    }
                });
    }

    /**
     * g 是“全局增量”命令；
     * 假设它的参数是一个数字，然后将该数字加到一个累加器上
     */
    private static void globalCounter(String val, GossipManager gossipManager) {
        // 取出对应 INDEX_KEY_FOR_COUNTER 为主键的只增计数器
        GrowOnlyCounter counter = (GrowOnlyCounter) gossipManager
                .findCrdt(INDEX_KEY_FOR_COUNTER);
        Long num = Long.valueOf(val);
        //不存在则创建新计数器，否则相加即可
        if (counter == null) {
            counter = new GrowOnlyCounter(new GrowOnlyCounter
                    .Builder(gossipManager).increment((num)));
        } else {
            counter = new GrowOnlyCounter(counter
                    , new GrowOnlyCounter.Builder(gossipManager)
                    .increment((num)));
        }
        //整合消息
        SharedDataMessage m = new SharedDataMessage();
        m.setExpireAt(Long.MAX_VALUE);
        m.setKey(INDEX_KEY_FOR_COUNTER);
        m.setPayload(counter);
        m.setTimestamp(System.currentTimeMillis());
        gossipManager.merge(m);
    }

    /**
     * r 是“删除”命令；它将字符串从集合中删除（如果它存在于集合中）
     */
    private static void removeData(String val, GossipManager gossipService) {
        @SuppressWarnings("unchecked")
        //取出对应 INDEX_KEY_FOR_SET 主键的OrSet数据集
                OrSet<String> orSet = (OrSet<String>) gossipService
                .findCrdt(INDEX_KEY_FOR_SET);
        SharedDataMessage message = new SharedDataMessage();
        message.setExpireAt(Long.MAX_VALUE);
        message.setKey(INDEX_KEY_FOR_SET);
        //将删除此数据后的新构造的OrSet打包进message
        message.setPayload(new OrSet<String>(orSet, new OrSet.Builder<String>().remove(val)));
        message.setTimestamp(System.currentTimeMillis());
        gossipService.merge(message);
    }


    /**
     * a 是“添加”命令；它将字符串添加到共享集合中
     *
     * @param val 参数、待添加字符串
     */
    private static void addData(String val, GossipManager gossipService) {
        //讲添加信息封装为一个 SharedDataMessage
        SharedDataMessage message = new SharedDataMessage();
        message.setExpireAt(Long.MAX_VALUE);
        //设置主键
        message.setKey(INDEX_KEY_FOR_SET);
        //装载 待添加字符串
        message.setPayload(new OrSet<String>(val));
        message.setTimestamp(System.currentTimeMillis());
        //gossipService整合消息
        gossipService.merge(message);
    }

}
