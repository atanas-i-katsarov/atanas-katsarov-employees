import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CsvStoreService } from '../../state/csv-store.service';

@Component({
  selector: 'app-pair-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pair-table.component.html',
  styleUrls: ['./pair-table.component.scss'],
})
export class PairTableComponent {
  constructor(private readonly csvStore: CsvStoreService) {}

  get results() {
    return this.csvStore.results;
  }

  get resultMessage() {
    return this.csvStore.resultMessage;
  }
}
