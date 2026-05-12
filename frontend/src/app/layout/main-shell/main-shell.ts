import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../component/sidebar/sidebar';

@Component({
  selector: 'app-main-shell',
  imports: [Sidebar, RouterOutlet],
  templateUrl: './main-shell.html',
  styleUrl: './main-shell.css',
})
export class MainShell {}
