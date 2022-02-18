package ru.kontur.chartographer.repository;

import org.springframework.stereotype.Repository;
import ru.kontur.chartographer.domain.Block;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dzahbarov
 */

@Repository
public class FileSystemRepository {

    private final BlockRepository blockRepository;

    public FileSystemRepository(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    String RESOURCES_DIR = FileSystemRepository.class.getResource("/")
            .getPath();

//    String RESOURCES_DIR = "/test/folder/";

    String save(byte[] content, String imageName) throws IOException {
        Path newFile = Paths.get(RESOURCES_DIR + new Date().getTime() + "-" + imageName);
        Files.createDirectories(newFile.getParent());

        Files.write(newFile, content);

        return newFile.toAbsolutePath()
                .toString();
    }

    public String save(BufferedImage image, String imageName) throws IOException {
        Path newFile = Paths.get(RESOURCES_DIR + new Date().getTime() + "-" + imageName);
        Files.createDirectories(newFile.getParent());
//        File outputfile = new File("image.jpg");
//        Files.write(newFile, content);
        File file = new File(String.valueOf(newFile));

        boolean x = ImageIO.write(image, "bmp", file);

        return newFile.toAbsolutePath()
                .toString();
    }

    public BufferedImage findInFileSystem(String location) {
        BufferedImage img = null;
        try {
            return ImageIO.read(new File(location));
        } catch (IOException e) {
        }
//        try {
//            return new FileSystemResource(Paths.get(location));
//        } catch (Exception e) {
//            // Handle access or file not found problems.
//            throw new RuntimeException();
//        }
        return img;
    }

    public List<Block> generateEmptyBlocks(int width, int height, long chartaId) throws IOException {
        List<Block> blocks = new ArrayList<>();

        long blockId = 0;

        while (height >= 5000) {
            Block block = createBlock(chartaId, blockId, width, 5000);
            blocks.add(block);
            height -= 5000;
            blockId++;
        }

        if (height > 0) {
            Block block = createBlock(chartaId, blockId, width, height);
            blocks.add(block);
        }

        return blocks;
    }

    private Block createBlock(long chartaId, long blockId, int width, int height) throws IOException {
        String newFile = RESOURCES_DIR + chartaId + '/' + new Date().getTime() + "-" + blockId;
        Files.createDirectories(Path.of(newFile).getParent());
        File file = new File(newFile);
        ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "bmp", file);
        Block block = new Block();
        block.setLocation(newFile);
        blockRepository.save(block);
        return block;
    }
}