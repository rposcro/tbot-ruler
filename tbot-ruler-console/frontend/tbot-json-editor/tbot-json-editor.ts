import {html, LitElement, PropertyValues, type TemplateResult} from 'lit';
import {createJSONEditor, JsonEditor} from 'vanilla-jsoneditor';

export class TbotJsonEditor extends LitElement {

    private jsonEditor! : JsonEditor;

    protected override createRenderRoot() {
        return this;
    }

    render(): TemplateResult {
        return html`
            <div id="tbot-json-editor-container"></div>
        `;
    }

    protected override firstUpdated(_changedProperties: PropertyValues) {
        super.firstUpdated(_changedProperties);

        let jsonContainer = this.renderRoot.querySelector('#tbot-json-editor-container') as HTMLDivElement;
        console.log(jsonContainer);

        this.jsonEditor = createJSONEditor({
            target: jsonContainer,
            props: {
                mode: 'tree'
            }
        });
    }
}

customElements.define('tbot-json-editor', TbotJsonEditor);
