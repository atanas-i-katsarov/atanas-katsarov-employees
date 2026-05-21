import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UploadFileComponent } from './components/upload-file/upload-file.component';
import { PairTableComponent } from './components/pair-table/pair-table.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, UploadFileComponent, PairTableComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Employees');
}
