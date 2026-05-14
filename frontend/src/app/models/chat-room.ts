export interface RegisteredUser {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface ChatMessage {
  id: number;
  message: string;
  sender: string;
  creationDate: string;
  messageType: string;
  room: string;
}
export interface ChatRoom {
  id: number;
  name: string;
  description: string;
  ownerId: string;
  registeredUsers: RegisteredUser[];
  chatMessages: ChatMessage[];
}
