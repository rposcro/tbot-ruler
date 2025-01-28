import {html, LitElement, PropertyValues, type TemplateResult} from 'lit';
import {property} from 'lit/decorators.js';
import {createJSONEditor, JSONContent, JsonEditor, OnChangeStatus, toTextContent} from 'vanilla-jsoneditor';

export class TbotJsonEditor extends LitElement {

    private jsonEditor!: JsonEditor;
    @property() currentJsonBody: string = '';

    public setJsonBody(jsonBody: string) {
        this.currentJsonBody = jsonBody;
        this.jsonEditor.set({
           text: this.currentJsonBody
        });
        this.dispatchEvent(new CustomEvent('json-updated'));
    }

    protected override createRenderRoot() {
        return this;
    }

    protected override firstUpdated(_changedProperties: PropertyValues) {
        super.firstUpdated(_changedProperties);

        let jsonContainer = this.renderRoot.querySelector('#tbot-json-editor-container') as HTMLDivElement;

        this.jsonEditor = createJSONEditor({
            target: jsonContainer,
            props: {
                mode: 'tree',
                content: {
                    text: this.currentJsonBody
                },
                onChange: (updatedContent: JSONContent, previousContent: JSONContent, status: OnChangeStatus) => {
                    this.currentJsonBody = toTextContent(updatedContent).text;
                    this.dispatchEvent(new CustomEvent('json-updated'));
                }
            }
        });
    }

    render(): TemplateResult {
        return html`
            <vaadin-button id='tbot-json-editor-button'>click</vaadin-button>
            <div id='tbot-json-editor-container'></div>
        `;
    }
}

customElements.define('tbot-json-editor', TbotJsonEditor);
