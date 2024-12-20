// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.fileEditor.impl;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApiStatus.Internal
public final class TestEditorSplitter {
  private final HashMap<String, TestEditorTabGroup> myTabGroups = new HashMap<>();
  private static final String Default = "Default";
  private String myActiveTabGroupName;

  public TestEditorSplitter() {
    myTabGroups.put(Default, new TestEditorTabGroup(Default));
    myActiveTabGroupName = Default;
  }

  private TestEditorTabGroup getActiveTabGroup() {
    return myTabGroups.get(myActiveTabGroupName);
  }

  public void openAndFocusTab(VirtualFile virtualFile, FileEditor fileEditor, FileEditorProvider provider) {
    getActiveTabGroup().openTab(virtualFile, fileEditor, provider);
  }

  public void setActiveTabGroup(@NotNull String tabGroup) {
    TestEditorTabGroup result = myTabGroups.get(tabGroup);
    if (result == null) {
      result = new TestEditorTabGroup(tabGroup);
      myTabGroups.put(tabGroup, result);
    }
    myActiveTabGroupName = tabGroup;
  }

  public @Nullable FileEditor getFocusedFileEditor() {
    Pair<FileEditor, FileEditorProvider> openedEditor = getActiveTabGroup().getOpenedEditor();
    if(openedEditor == null)
      return null;

    return openedEditor.first;
  }

  public @Nullable FileEditorProvider getProviderFromFocused() {
    Pair<FileEditor, FileEditorProvider> openedEditor = getActiveTabGroup().getOpenedEditor();
    if(openedEditor == null)
      return null;

    return openedEditor.second;
  }

  public VirtualFile getFocusedFile() {
    return getActiveTabGroup().getOpenedFile();
  }

  public void setFocusedFile(VirtualFile file) {
    for (TestEditorTabGroup group : myTabGroups.values()) {
      if (group.contains(file)) {
        setActiveTabGroup(group.getName());
        group.setOpenedFile(file);
        break;
      }
    }
  }

  public void closeFile(@NotNull VirtualFile file) {
    TestEditorTabGroup testEditorTabGroup = getActiveTabGroup();
    String key = myActiveTabGroupName;
    if (!testEditorTabGroup.contains(file)) {
      for (Map.Entry<String, TestEditorTabGroup> next : myTabGroups.entrySet()) {
        key = next.getKey();
        TestEditorTabGroup value = next.getValue();
        if (value.contains(file)) {
          testEditorTabGroup = value;
          break;
        }
      }
    }
    testEditorTabGroup.closeTab(file);
    if (!Objects.equals(key, Default) && testEditorTabGroup.getTabCount() == 0)
      myTabGroups.remove(key);
  }

  public @Nullable Pair<FileEditor, FileEditorProvider> getEditorAndProvider(VirtualFile file) {
    return getActiveTabGroup().getEditorAndProvider(file);
  }
}
