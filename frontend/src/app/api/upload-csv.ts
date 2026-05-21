import { EmployeeCsvResponse } from "../model/types";

const DEFAULT_UPLOAD_ENDPOINT = 'http://localhost:8080/api/employees/upload';

export async function uploadCsvFile(
  file: File,
  endpoint = DEFAULT_UPLOAD_ENDPOINT,
): Promise<EmployeeCsvResponse> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(endpoint, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`CSV upload failed: ${response.status} ${response.statusText} - ${errorText}`);
  }

  return response.json() as Promise<EmployeeCsvResponse>;
}
