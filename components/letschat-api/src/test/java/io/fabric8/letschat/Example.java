/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.letschat;

import io.fabric8.kubernetes.api.KubernetesClient;

import java.util.List;
import java.util.Map;

/**
 */
public class Example {
    public static void main(String[] args) {
        String roomName = "fabric8_default";
        if (args.length > 0) {
            roomName = args[0];
        }

        try {
            KubernetesClient kubernetes = new KubernetesClient();
            LetsChatClient letschat = LetsChatKubernetes.createLetsChat(kubernetes);

            System.out.println("Connecting to letschat on: " + letschat.getAddress());

            List<RoomDTO> rooms = letschat.getRooms();
            for (RoomDTO room : rooms) {
                System.out.println("Room " + room.getId() + " has slug: " + room.getSlug() + " name " + room.getName());
            }

            // looking up a room
            RoomDTO myRoom = letschat.getRoom(roomName);
            System.out.println("Found room: " + myRoom + " by slug: " + roomName);

            RoomDTO notExist = letschat.getRoom("does-not-exist");
            System.out.println("Found non existing room: " + notExist);

            // lets try lazily create a room if it doesn't exist
            RoomDTO newRoom = letschat.getOrCreateRoom("my_new_room_slug");
            System.out.println("Found/created room: " + newRoom);

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
