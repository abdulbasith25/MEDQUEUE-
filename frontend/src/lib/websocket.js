import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const createWebSocketClient = (onConnect, onUpdate, doctorId) => {
  const socket = new SockJS(`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/ws`);
  
  const client = new Client({
    webSocketFactory: () => socket,
    debug: (str) => {
      console.log('STOMP: ' + str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    onConnect();
    
    // Subscribe to doctor's queue
    if (doctorId) {
      client.subscribe(`/topic/queue/${doctorId}`, (message) => {
        if (message.body) {
          onUpdate(JSON.parse(message.body));
        }
      });
    }
  };

  client.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
  };

  return client;
};
