/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.integration.repository;

import com.borak.kinweb.backend.ConfigPropertiesTest;
import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.domain.jdbc.classes.PersonJDBC;
import com.borak.kinweb.backend.exceptions.DatabaseException;
import com.borak.kinweb.backend.helpers.DataInitializer;
import com.borak.kinweb.backend.integration.domain.MyImageTest;
import com.borak.kinweb.backend.repository.util.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Mr. Poyo
 */
@SpringBootTest
@ActiveProfiles("test")
@Order(4)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileRepositoryTest {

    @Autowired
    private FileRepository repo;

    private DataInitializer init = new DataInitializer();

    private static final Map<String, Boolean> testsPassed = new HashMap<>();

    static {
        testsPassed.put("saveMediaCoverImage_Test", false);
        testsPassed.put("savePersonProfilePhoto_Test", false);
        testsPassed.put("deleteIfExistsMediaCoverImage_Test", false);
        testsPassed.put("deleteIfExistsPersonPhotoImage_Test", false);
    }

    public static boolean didAllTestsPass() {
        for (boolean b : testsPassed.values()) {
            if (!b) {
                return false;
            }
        }
        return true;
    }
//=================================================================================================

    @BeforeEach
    void beforeEach() {
        Assumptions.assumeTrue(ConfigPropertiesTest.didAllTestsPass());
        Assumptions.assumeTrue(MyImageTest.didAllTestsPass());
        init.initImages();
    }

    @Test
    @Order(1)
    void saveMediaCoverImage_Test() {
        assertThatExceptionOfType(DatabaseException.class).isThrownBy(() -> {
            repo.saveMediaCoverImage(null);
        }).withMessage("Unable to save media cover image");

        MyImage image = new MyImage(new MockMultipartFile("cover_image", "dummy.jpg", "image/jpg", new byte[10]));
        repo.saveMediaCoverImage(image);
        File file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        Path path = Paths.get(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        image = new MyImage(new MockMultipartFile("cover_image", "  dum  my  .jpg", "image/jpg", new byte[10]));
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        path = Paths.get(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        image = new MyImage(new MockMultipartFile("cover_image", "http://www.website.com/images/  dum  my  .jpg", "image/jpg", new byte[10]));
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        path = Paths.get(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        String name = "cover_image";
        String originalFileName = init.getMullholadDrive().getCoverImage();
        path = Paths.get(DataInitializer.mediaImagesFolderPath + originalFileName);
        String contentType = "image/" + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }
        image = new MyImage(new MockMultipartFile(name, originalFileName, contentType, content));
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        name = "cover_image";
        originalFileName = init.getInlandEmpire().getCoverImage();
        path = Paths.get(DataInitializer.mediaImagesFolderPath + originalFileName);
        contentType = "image/" + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }
        image = new MyImage(new MockMultipartFile(name, originalFileName, contentType, content));
        image.setName("dummy_name_2");
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        path = Paths.get(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        name = "cover_image";
        originalFileName = init.getInlandEmpire().getCoverImage();
        path = Paths.get(DataInitializer.mediaImagesFolderPath + originalFileName);
        contentType = "image/" + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }
        image = new MyImage(new MockMultipartFile(name, originalFileName, contentType, content));
        image.setName("");
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        path = Paths.get(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        assertThat(file.isDirectory()).isFalse();
        assertThat(file.getName()).isEqualTo(image.getFullName());
        try {
            assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        testsPassed.put("saveMediaCoverImage_Test", true);
    }

    @Test
    @Order(2)
    void savePersonProfilePhoto_Test() {
        assertThatExceptionOfType(DatabaseException.class).isThrownBy(() -> {
            repo.savePersonProfilePhoto(null);
        }).withMessage("Unable to save person profile photo");

        String[][] validInput = new String[8][2];
        validInput[0] = new String[]{"dummy.jpg", "image/jpg"};
        validInput[1] = new String[]{"  dum  my  .jpg", "image/jpg"};
        validInput[2] = new String[]{"http://www.website.com/images/  dum  my  .jpg", "image/jpg"};
        validInput[3] = new String[]{".jpg", "image/jpg"};
        validInput[4] = new String[]{"dummy.png", "image/png"};
        validInput[5] = new String[]{"dummy.jpg", "image/png"};
        validInput[6] = new String[]{"dummy.png", "image/jpg"};
        validInput[7] = new String[]{".png", "image/png"};

        MyImage image = null;
        File file = null;
        Path path = null;
        for (int i = 0; i < validInput.length; i++) {
            image = new MyImage(new MockMultipartFile("cover_image", validInput[i][0], validInput[i][1], new byte[10]));
            repo.savePersonProfilePhoto(image);
            file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
            path = Paths.get(DataInitializer.personImagesFolderPath + image.getFullName());
            assertThat(file.exists()).as("At i value: %s", i).isTrue();
            assertThat(file.isDirectory()).as("At i value: %s", i).isFalse();
            assertThat(file.getName()).as("At i value: %s", i).isEqualTo(image.getFullName());
            try {
                assertThat(image.getBytes()).as("At i value: %s", i).isEqualTo(Files.readAllBytes(path));
            } catch (IOException e) {
                fail("Files.readAllBytes() was not supposed to throw exception");
            }
        }

        String originalFileName;
        String contentType;
        for (PersonJDBC person : init.getPersons()) {
            originalFileName = person.getProfilePhoto();
            if (originalFileName != null) {
                path = Paths.get(DataInitializer.personImagesFolderPath + originalFileName);
                contentType = "image/" + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                byte[] content = null;
                try {
                    content = Files.readAllBytes(path);
                } catch (IOException e) {
                    fail("Files.readAllBytes() was not supposed to throw exception");
                }
                image = new MyImage(new MockMultipartFile("cover_image", originalFileName, contentType, content));

                //save image to an already existing one
                repo.savePersonProfilePhoto(image);
                file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
                assertThat(file.exists()).isTrue();
                assertThat(file.isDirectory()).isFalse();
                assertThat(file.getName()).isEqualTo(image.getFullName());
                try {
                    assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
                } catch (IOException e) {
                    fail("Files.readAllBytes() was not supposed to throw exception");
                }

                //change name of image, and save it as a new one
                image.setName("dummy_name_2");
                repo.savePersonProfilePhoto(image);
                file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
                path = Paths.get(DataInitializer.personImagesFolderPath + image.getFullName());
                assertThat(file.exists()).isTrue();
                assertThat(file.isDirectory()).isFalse();
                assertThat(file.getName()).isEqualTo(image.getFullName());
                try {
                    assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
                } catch (IOException e) {
                    fail("Files.readAllBytes() was not supposed to throw exception");
                }

                image.setName("");
                repo.savePersonProfilePhoto(image);
                file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
                path = Paths.get(DataInitializer.personImagesFolderPath + image.getFullName());
                assertThat(file.exists()).isTrue();
                assertThat(file.isDirectory()).isFalse();
                assertThat(file.getName()).isEqualTo(image.getFullName());
                try {
                    assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
                } catch (IOException e) {
                    fail("Files.readAllBytes() was not supposed to throw exception");
                }
            }
        }

        MyImage imageJPG = new MyImage(new MockMultipartFile("cover_image", "dummy.jpg", "image/jpg", new byte[10]));
        MyImage imagePNG = new MyImage(new MockMultipartFile("cover_image", "dummy.png", "image/png", new byte[20]));
        repo.savePersonProfilePhoto(imageJPG);
        repo.savePersonProfilePhoto(imagePNG);
        File fileJPG = new File(DataInitializer.personImagesFolderPath + imageJPG.getFullName());
        File filePNG = new File(DataInitializer.personImagesFolderPath + imagePNG.getFullName());
        Path pathJPG = Paths.get(DataInitializer.personImagesFolderPath + imageJPG.getFullName());
        Path pathPNG = Paths.get(DataInitializer.personImagesFolderPath + imagePNG.getFullName());

        assertThat(fileJPG.exists()).isTrue();
        assertThat(fileJPG.isDirectory()).isFalse();
        assertThat(fileJPG.getName()).isEqualTo(imageJPG.getFullName());

        assertThat(filePNG.exists()).isTrue();
        assertThat(filePNG.isDirectory()).isFalse();
        assertThat(filePNG.getName()).isEqualTo(imagePNG.getFullName());

        try {
            assertThat(imageJPG.getBytes()).isEqualTo(Files.readAllBytes(pathJPG));
            assertThat(imagePNG.getBytes()).isEqualTo(Files.readAllBytes(pathPNG));
            assertThat(Files.readAllBytes(pathJPG)).isNotEqualTo(Files.readAllBytes(pathPNG));
        } catch (IOException e) {
            fail("Files.readAllBytes() was not supposed to throw exception");
        }

        testsPassed.put("savePersonProfilePhoto_Test", true);
    }

    @Test
    @Order(3)
    void deleteIfExistsMediaCoverImage_Test() {
        Assumptions.assumeTrue(testsPassed.get("saveMediaCoverImage_Test"));

        String[] invalidInput = new String[]{null, "", " ", "        "};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsMediaCoverImage(input);
            }).withMessage("Invalid argument: image name must not be null or blank!");
        }
        invalidInput = new String[]{"jpg", "aaaaaaaa", "..", ".jpg.", ".png.", ".", " . ", "jpg.", "png.",
            "http://www.website.com/images/.jpg.", ".jpg/", ".jpg/website", ".jpg/jpg", "..jpg", "aaa aaa aaa..jpg"};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsMediaCoverImage(input);
            }).withMessage("Invalid argument: unable to infer image extension!");
        }

        invalidInput = new String[]{".gpj", ".g", "aaaaa.g", "  a  aa  aa.g", "http://www.google.com/images/aaaa.jgp"};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsMediaCoverImage(input);
            }).withMessage("Invalid argument: unrecognized image extension! Image extension must be one of following: " + MyImage.VALID_EXTENSIONS);
        }

        String fileName = init.getMullholadDrive().getCoverImage();
        File file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isTrue();
        repo.deleteIfExistsMediaCoverImage(fileName);
        file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isFalse();

        fileName = init.getInlandEmpire().getCoverImage();
        file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isTrue();
        repo.deleteIfExistsMediaCoverImage(fileName);
        file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isFalse();

        fileName = "NonExistantFileName.jpg";
        file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isFalse();
        repo.deleteIfExistsMediaCoverImage(fileName);
        file = new File(DataInitializer.mediaImagesFolderPath + fileName);
        assertThat(file.exists()).isFalse();

        MyImage image = new MyImage(new MockMultipartFile("cover_image", "  dum  my  .jpg", "image/jpg", new byte[10]));
        repo.saveMediaCoverImage(image);
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isTrue();
        repo.deleteIfExistsMediaCoverImage(image.getFullName());
        file = new File(DataInitializer.mediaImagesFolderPath + image.getFullName());
        assertThat(file.exists()).isFalse();

        testsPassed.put("deleteIfExistsMediaCoverImage_Test", true);
    }

    @Test
    @Order(4)
    void deleteIfExistsPersonPhotoImage_Test() {
        Assumptions.assumeTrue(testsPassed.get("savePersonProfilePhoto_Test"));

        String[] invalidInput = new String[]{null, "", " ", "        "};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsPersonPhotoImage(input);
            }).withMessage("Invalid argument: image name must not be null or blank!");
        }
        invalidInput = new String[]{"jpg", "aaaaaaaa", "..", ".jpg.", ".png.", ".", " . ", "jpg.", "png.",
            "http://www.website.com/images/.jpg.", ".jpg/", ".jpg/website", ".jpg/jpg", "..jpg", "aaa aaa aaa..jpg"};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsPersonPhotoImage(input);
            }).withMessage("Invalid argument: unable to infer image extension!");
        }

        invalidInput = new String[]{".gpj", ".g", "aaaaa.g", "  a  aa  aa.g", "http://www.google.com/images/aaaa.jgp"};
        for (int iter = 0; iter < invalidInput.length; iter++) {
            final int i = iter;
            final String input = invalidInput[i];
            assertThatExceptionOfType(IllegalArgumentException.class).as("Code number(i) value (%s)", i).isThrownBy(() -> {
                repo.deleteIfExistsPersonPhotoImage(input);
            }).withMessage("Invalid argument: unrecognized image extension! Image extension must be one of following: " + MyImage.VALID_EXTENSIONS);
        }

        String fileName;
        File file;
        MyImage image;
        for (PersonJDBC person : init.getPersons()) {
            if (person.getProfilePhoto() != null) {
                fileName = person.getProfilePhoto();
                file = new File(DataInitializer.personImagesFolderPath + fileName);
                assertThat(file.exists()).isTrue();
                repo.deleteIfExistsPersonPhotoImage(fileName);
                file = new File(DataInitializer.personImagesFolderPath + fileName);
                assertThat(file.exists()).isFalse();

                fileName = "NonExistantFileName.jpg";
                file = new File(DataInitializer.personImagesFolderPath + fileName);
                assertThat(file.exists()).isFalse();
                repo.deleteIfExistsPersonPhotoImage(fileName);
                file = new File(DataInitializer.personImagesFolderPath + fileName);
                assertThat(file.exists()).isFalse();

                image = new MyImage(new MockMultipartFile("cover_image", "  dum  my  .jpg", "image/jpg", new byte[10]));
                repo.savePersonProfilePhoto(image);
                file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
                assertThat(file.exists()).isTrue();
                repo.deleteIfExistsPersonPhotoImage(image.getFullName());
                file = new File(DataInitializer.personImagesFolderPath + image.getFullName());
                assertThat(file.exists()).isFalse();
            }
        }

        testsPassed.put("deleteIfExistsPersonPhotoImage_Test", true);
    }

}
