import { inject, Injectable } from '@angular/core';
import { Client, IMessage, StompConfig, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { AuthService } from "./auth-service";

export enum ChatMessageType {
  CHAT_MESSAGE = 'CHAT_MESSAGE',
  JOIN = 'JOIN',
  LEAVE = 'LEAVE',
}

export interface ChatMessagePayload {
  id?: number;
  message?: string;
  sender?: string;
  creationDate?: string;
  messageType: ChatMessageType;
  roomId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ChatSocketService {

  private readonly auth = inject(AuthService);

  private client!: Client;
  private isConnected = false;
  private roomSubscriptions = new Map<number, StompSubscription>();
  private incoming$ = new Subject<{ roomId: number; payload: ChatMessagePayload }>();

  public get messages$(): Observable<{ roomId: number; payload: ChatMessagePayload }> {
    return this.incoming$.asObservable();
  }

  connect(token: string | null): void {
    if (this.client) {
      this.client.deactivate();
    }

    this.client = new Client({
      brokerURL: 'ws://127.0.0.1:8081/ws',
      reconnectDelay: 5000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      onConnect: () => {
        console.log('CONNECTED');
        this.isConnected = true;
      },
      onDisconnect: () => {
        console.log('DISCONNECTED');
        this.isConnected = false;
      }
    });

    this.client.activate();
  }

  disconnect(): void {
    if (!this.client) return;

    this.roomSubscriptions.forEach((sub) => sub.unsubscribe());
    this.roomSubscriptions.clear();
    this.client.deactivate();
    this.isConnected = false;
  }

  subscribeToRoom(roomId: number): void {
    if (!this.client) {
      console.warn('WebSocket client not initialized');
      return;
    }

    if (this.roomSubscriptions.has(roomId)) {
      return;
    }

    const subscription = this.client.subscribe(`/topic/messages/${roomId}`, (msg: IMessage) => {
      try {
        const payload: ChatMessagePayload = JSON.parse(msg.body);
        this.incoming$.next({ roomId, payload });
      } catch (e) {
        console.error('Failed to parse incoming message', e);
      }
    });

    this.roomSubscriptions.set(roomId, subscription);

    this.sendMessage(roomId, {
      messageType: ChatMessageType.JOIN,
      roomId: roomId,
    });
  }

  unsubscribeFromRoom(roomId: number): void {
    const sub = this.roomSubscriptions.get(roomId);
    if (!sub) return;

    sub.unsubscribe();
    this.roomSubscriptions.delete(roomId);

    this.sendMessage(roomId, {
      messageType: ChatMessageType.LEAVE,
      roomId: roomId,
    });
  }

  sendMessage(roomId: number, payload: ChatMessagePayload): void {
    if (!this.client || !this.isConnected) {
      console.warn('Cannot send message, WebSocket not connected');
      return;
    }

    console.log(payload);
    this.client.publish({
      destination: `/app/chat/${roomId}`,
      body: JSON.stringify(payload),
    });
  }
}
