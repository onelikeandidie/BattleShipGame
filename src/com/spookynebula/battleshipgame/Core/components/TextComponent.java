package com.spookynebula.battleshipgame.Core.components;

import com.spookynebula.battleshipgame.ECS.Component;

public class TextComponent extends Component {
    protected String text;

    public TextComponent(){
        enabled = true;
        type = "text_component";
        ID = 0;

        text = "";
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TextComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", text='" + text + '\'' +
                '}';
    }
}
