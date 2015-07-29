package com.smartcodeltd;

import com.smartcodeltd.sugar.Property;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static com.smartcodeltd.sugar.Property.property;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UpdateVersionMojoTest {
    
    @Rule public final TestProjectResources resource = new TestProjectResources("src/test/resources/projects", "target/projects");
    @Rule public final Mojos mojo = new Mojos("updateVersion", resource);

    private Log log;

    private Mojo releaseCandidateUpdateVersion;

    private final ByteArrayOutputStream stdOutContent = new ByteArrayOutputStream();
    
    @Before
    public void each_test() {
        log = mock(Log.class);

        System.setOut(new PrintStream(stdOutContent));
    }

    @After
    public void clean_up() {
        System.setOut(null);
    }

    @Test
    public void keeps_pom_xml_unchanged_if_no_config_specified() throws Exception {
        releaseCandidateUpdateVersion = mojo.forProject("out-of-the-box");

        String originalContent = resource.contentOf("out-of-the-box", "pom.xml");

        releaseCandidateUpdateVersion.execute();

        assertThat(resource.contentOf("out-of-the-box", "pom.xml"), is(originalContent));
    }

    @Test
    public void updates_pom_xml_version_as_per_the_version_format() throws Exception {
        releaseCandidateUpdateVersion = mojo.forProject("updating-the-version", with(
            property("build_number", "2"),
            property("git_commit", "16f0eb28")
        ));

        releaseCandidateUpdateVersion.execute();

        assertThat(
            mojo.mavenProjectFor("updating-the-version").getVersion(),
            is("1.7.2-build.2.sha.16f0eb28")
        );
    }

    @Test
    public void notifies_the_user_of_new_version() throws Exception {
        releaseCandidateUpdateVersion = mojo.forProject("updating-the-version", with(
                property("build_number", "2"),
                property("git_commit", "16f0eb28")
        ));

        releaseCandidateUpdateVersion.setLog(log);

        releaseCandidateUpdateVersion.execute();

        verify(log).info("Setting version to: '1.7.2-build.2.sha.16f0eb28'");
        verifyNoMoreInteractions(log);
    }

    @Test
    public void backs_up_the_original_pom_xml_file() throws Exception {
        // todo: implement
    }

    @Test
    public void complains_if_no_parameters_are_provided() throws Exception {
        // todo: implement
    }



    // --

    private void givenConfigured(String field, Object value) throws IllegalAccessException {
        mojo.given(releaseCandidateUpdateVersion, field, value);
    }

    private List<Property> with(Property... properties) {
        return Arrays.asList(properties);
    }
}