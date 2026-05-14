import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ChatSocketService } from "./services/websocket.service";
import { AuthService } from "./services/auth-service";
import { ChatRoomStateService } from "./services/chat-state.service";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly chatService = inject(ChatRoomStateService)
  constructor(private socket: ChatSocketService) {
    this.socket.connect(this.authService.getStoredToken());
    this.chatService.loadRooms();
  }
}
