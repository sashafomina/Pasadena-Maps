package edu.caltech.cs2.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.util.stream.Stream;


public class ImageFileSourceProvider implements ArgumentsProvider, AnnotationConsumer<ImageFileSource> {
  private String[] inputs;
  private String[] outputFiles;


  @Override
  public void accept(ImageFileSource source) {
    this.inputs = source.inputs();
    this.outputFiles = source.outputFiles();
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    Arguments[] args = new Arguments[this.outputFiles.length];
    for (int i = 0; i < this.outputFiles.length; i++) {
      String inputArgs = this.inputs[i];
      args[i] = Arguments.arguments(inputArgs, Images.getImage("tests/data/" + this.outputFiles[i]));
    }
    return Stream.of(args);
  }


}
