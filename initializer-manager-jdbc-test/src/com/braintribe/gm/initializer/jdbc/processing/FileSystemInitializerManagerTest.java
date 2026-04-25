package com.braintribe.gm.initializer.jdbc.processing;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link FileSystemInitializerManager}
 * 
 * @author peter.gazdik
 */
public class FileSystemInitializerManagerTest extends AbstractInitializerManagerTest<FileSystemInitializerManager> {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private File storageFile;

	@Override
	@Before
	public void before() {
		storageFile = new File(tempFolder.getRoot(), "hiconic/test/initializer-fingerprints.properties");
		super.before();
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	// @formatter:off
	@Override @Test public void noTasks_RunsWithoutError() { super.noTasks_RunsWithoutError(); }
	@Override @Test public void singleTask_RunsOnce() { super.singleTask_RunsOnce(); }
	@Override @Test public void singleTask_SkippedWhenFingerprintUnchanged() { super.singleTask_SkippedWhenFingerprintUnchanged(); }
	@Override @Test public void singleTask_RerunsWhenFingerprintChanges() { super.singleTask_RerunsWhenFingerprintChanges(); }
	@Override @Test public void singleTask_RerunsFirstThenSkipped() { super.singleTask_RerunsFirstThenSkipped(); }
	@Override @Test public void taskOrdering_DependencyRunsFirst() { super.taskOrdering_DependencyRunsFirst(); }
	@Override @Test public void taskOrdering_CycleDetected() { super.taskOrdering_CycleDetected(); }
	@Override @Test public void failingTask_DoesNotUpdateFingerprint() { super.failingTask_DoesNotUpdateFingerprint(); }
	@Override @Test public void exceptionInTask_DoesNotUpdateFingerprint() { super.exceptionInTask_DoesNotUpdateFingerprint(); }
	// @formatter:on

	@Override
	protected FileSystemInitializerManager newManager() {
		FileSystemInitializerManager m = new FileSystemInitializerManager();
		m.setUseCase("test");
		m.setTasksPropertiesFile(storageFile);
		return m;
	}

}
