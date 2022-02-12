package com.trunksku.asmobile.compile.builder.task;

import com.trunksku.asmobile.compile.builder.IBuilder;
import com.trunksku.asmobile.compile.builder.Task;

public class CleanTask extends Task {

    public CleanTask(IBuilder builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "Clean";
    }

    @Override
    public boolean doFullTaskAction() throws Exception {
        //mProject.clean();
        return true;
    }
}

