package com.intellij.flex.uiDesigner;

import com.intellij.flex.model.bc.OutputType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.TripleFunction;

import java.io.IOException;
import java.util.List;

@Flex(version="4.5")
public class StyleTest extends MxmlTestBase {
  @Override
  protected String getSourceBasePath() {
    return getName().equals("testAlwaysCreateChildStyleManagerForAppDocument") ? "AlwaysCreateChildStyleManagerForAppDocument" : "css";
  }
  
  public void testLibraryCssDefaults() throws Exception {
    testFile("emptyForCheckLibrariesCssDefaults.mxml");
  }

  @Override
  protected void assertAfterInitLibrarySets(List<XmlFile> unregisteredDocumentReferences) throws IOException {
    super.assertAfterInitLibrarySets(unregisteredDocumentReferences);

    if (getName().equals("testStyleTag")) {
      final ProblemsHolder problemsHolder = new ProblemsHolder();
      client.registerDocumentReferences(unregisteredDocumentReferences, myModule, problemsHolder);
      assertTrue(problemsHolder.isEmpty());
    }
  }
  
  @Flex(requireLocalStyleHolder=true, rawProjectRoot=true)
  public void testStyleTag() throws Exception {
    testFile("StyleTag.mxml", "testPackage/CustomSkinInPackage.mxml");
  }

  @Flex(version="4.6", requireLocalStyleHolder=true, rawProjectRoot=true)
  public void testStyleTag_46() throws Exception {
    testFile("StyleTag.mxml", "testPackage/CustomSkinInPackage.mxml");
  }

  @Flex(version="4.1", requireLocalStyleHolder=true)
  // see mx.controls.ButtonBar line 528 in flex sdk 4.1
  public void testMxButtonBar41WithLocalStyleHolder() throws Exception {
    moduleInitializer = (model, sourceDir, libs1) -> {
      final VirtualFile assetsDir = DesignerTests.getFile("assets");
      model.addContentEntry(assetsDir).addSourceFolder(assetsDir, false);
      return null;
    };

    // must be tested with local style holder
    testFile("../mx/MxComponents.mxml", "StyleTagWithSource.mxml", "externalCss.css");
  }
  
  @Flex(requireLocalStyleHolder=true)
  public void testStyleTagWithSource() throws Exception {
    testFile("StyleTagWithSource.mxml", "externalCss.css");
  }

  @Flex(requireLocalStyleHolder = true)
  public void testStyleTagWithSourceAsRelativePath() throws Exception {
    testFile("StyleTagWithSourceAsRelativePath.mxml", "externalCss.css");
  }

  @Flex(requireLocalStyleHolder=true)
  public void testApplicationLevelGlobalSelector() throws Exception {
    testFile("ApplicationLevelGlobalSelector.mxml");
  }

  @Flex(rawProjectRoot=true)
  public void testComponentWithCustomSkin() throws Exception {
    testFiles(new String[]{"ComponentWithCustomSkin.mxml", "ComponentWithCustomSkinInPackage.mxml", "ComponentWithCustomSkinAsBinding.mxml"}, "CustomSkin.mxml", "AuxMyButtonSkin.mxml", "testPackage/CustomSkinInPackage.mxml");
  }
  
  @Flex(requireLocalStyleHolder = true)
  public void testLibraryWithDefaultsCss() throws Exception {
    testFile("LibraryWithDefaultsCss.mxml", "defaults.css");
  }

  @Flex(requireLocalStyleHolder = true)
  public void testSeveralStyleSources() throws Exception {
    testFile("SeveralStyleSources.mxml", "defaults.css");
  }

  @Flex(requireLocalStyleHolder = true)
  public void testAlwaysCreateChildStyleManagerForAppDocument() throws Exception {
    testFiles(new String[]{"App2.mxml", "App1.mxml"}, "../css/defaults.css");
  }

  @Override
  protected OutputType getOutputType() {
    return getName().equals("testLibraryWithDefaultsCss") ? OutputType.Library : super.getOutputType();
  }
}