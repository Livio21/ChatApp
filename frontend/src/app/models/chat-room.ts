export interface RegisteredUser {
  id: number;
  username: string;
  email: string;
}

export interface ChatMessage {
  id: number;
  message: string;
  sender: string;
  creationDate: string;
  messageType: string;
  roomId: number;
}

export interface ChatRoom {
  id: number;
  name: string;
  description: string;
  owner: RegisteredUser;
  registeredUsers: RegisteredUser[];
  chatMessages: ChatMessage[];
}
