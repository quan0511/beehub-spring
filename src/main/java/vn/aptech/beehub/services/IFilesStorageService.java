package vn.aptech.beehub.services;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;

public interface IFilesStorageService {
	public void init();
	public Resource load(String filename);
	public Stream<Path> loadAll();
}
