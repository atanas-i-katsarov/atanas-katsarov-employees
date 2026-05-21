import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { uploadCsvFile } from '../../api/upload-csv';
import { CsvStoreService } from '../../state/csv-store.service';

@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.scss']
})
export class UploadFileComponent {
  constructor(private readonly csvStore: CsvStoreService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    this.csvStore.clearResults();

    uploadCsvFile(file!)
      .then(response => {
        this.csvStore.setResults(response);
      })
      .catch(error => {
        console.error('Upload failed:', error);
        this.csvStore.setResultMessage('Failed to upload CSV file. Please try again.');
      });
  }
}
