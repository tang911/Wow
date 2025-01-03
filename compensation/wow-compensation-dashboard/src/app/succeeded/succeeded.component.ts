import {Component} from '@angular/core';
import {FindCategory} from "../api/CompensationClient";
import {FailedListComponent} from "../failed-list/failed-list.component";

@Component({
    selector: 'app-succeeded',
    imports: [
        FailedListComponent
    ],
    templateUrl: './succeeded.component.html',
    styleUrl: './succeeded.component.scss'
})
export class SucceededComponent {

  protected readonly FindCategory = FindCategory;
}
