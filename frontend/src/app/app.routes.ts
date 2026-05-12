import { Routes } from '@angular/router';
import { SignIn } from './auth/sign-in/sign-in';
import { SignUp } from './auth/sign-up/sign-up';
import { authGuard } from './guards/auth-guard';
import { guestGuard } from './guards/guest-guard';
import { MainShell } from './layout/main-shell/main-shell';
import { Home } from './pages/home/home';
import { RoomChat } from './pages/room-chat/room-chat';
import { RoomMembers } from './pages/room-members/room-members';

export const routes: Routes = [
  { path: 'sign-in', component: SignIn, canActivate: [guestGuard] },
  { path: 'sign-up', component: SignUp, canActivate: [guestGuard] },
  {
    path: '',
    component: MainShell,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'home' },
      { title: 'Home', path: 'home', component: Home },
      { title: 'Room', path: 'rooms/:roomId', component: RoomChat },
      { title: 'Members', path: 'rooms/:roomId/members', component: RoomMembers },
    ],
  },
];
