import { Injectable, Signal, signal } from '@angular/core';
import { EmployeeCsvResponse } from '../model/types';

@Injectable({
  providedIn: 'root'
})
export class CsvStoreService {
  private readonly _results = signal<EmployeeCsvResponse | null>(null);
  readonly results: Signal<EmployeeCsvResponse | null> = this._results;

  
  private readonly _resultMessage = signal<string | null>("No CSV response available yet.");
  readonly resultMessage: Signal<string | null> = this._resultMessage;

  setResults(response: EmployeeCsvResponse) {
    this._results.set(response);
    this._resultMessage.set(null);
  }

  clearResults() {
    this._results.set(null);
    this._resultMessage.set(null);
  }

  setResultMessage(message: string | null) {
    this._resultMessage.set(message);
  }
}
