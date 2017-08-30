package com.epoch;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.*;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EpochReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        final EpochReference[] result = {null};
        if (psiElement instanceof XmlTagImpl) {
            XmlTagImpl tag = (XmlTagImpl) psiElement;
            if (tag.getName().equals("Call")) {
                XmlAttribute attribute = tag.getAttribute("name");
                if (attribute != null) {
                    XmlAttributeValue nameAttribute = attribute.getValueElement();
                    if (nameAttribute != null) {

                        PsiElement[] children = nameAttribute.getChildren();
                        if (children.length > 1) {
                            XmlTokenImpl token = (XmlTokenImpl) children[1];
                            if (token.getTokenType() == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
                                Project project = psiElement.getProject();
                                Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, XmlFileType.INSTANCE,
                                        GlobalSearchScope.allScope(project));
                                for (VirtualFile file : files) {
                                    XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(file);
                                    if (xmlFile != null) {
                                        System.out.println("Got file " + xmlFile.getName());
                                        xmlFile.accept(new PsiRecursiveElementVisitor() {
                                            @Override
                                            public void visitElement(PsiElement element) {
                                                super.visitElement(element);
                                                if (element instanceof XmlTagImpl) {
                                                    XmlTagImpl targetTag = (XmlTagImpl) element;
                                                    String procName = token.getText();
                                                    if ("Procedure".equals(targetTag.getName()) && procName.equals(targetTag.getAttributeValue("name"))) {
                                                        System.out.println("Found procedure");
                                                        System.out.println("proc name is " + procName);
                                                        int offset = nameAttribute.getStartOffsetInParent()+7; // The 7 for name=" - can be done properly later
                                                        System.out.println("textrange is " + new TextRange(offset, offset+token.getTextLength()));
                                                        result[0] = new EpochReference(tag, new TextRange(offset, offset+token.getTextLength()), procName, targetTag);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
