package com.xworkz.dream.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.xworkz.dream.dto.utils.AuthenticationGoogleDrive;

@Service
public class Generation {
	@Autowired
	private AuthenticationGoogleDrive service;

	private static String folderId = "18vyXDydstZeifHwmd2HbRddlbetQVoxr";

	public void googlegetFile() throws GeneralSecurityException, IOException {
		Drive serviceDrive = service.getInstance();
//		File execute = serviceDrive.files().get("1E1Jf2K3v5CQUTgc2eXt5K48o7xmawKt9").execute();
		OutputStream outputStream = new ByteArrayOutputStream();

		serviceDrive.files().get("1E1Jf2K3v5CQUTgc2eXt5K48o7xmawKt9").executeMediaAndDownloadTo(outputStream);
		System.err.println(outputStream);
	}

	public void googleSheetService() throws IOException, GeneralSecurityException {
		Drive serviceDrive = service.getInstance();
		java.io.File ref = new java.io.File("E://javapicture/a.pdf");
		File fileMetadata = new File();
		fileMetadata.setName(ref.getName());

		fileMetadata.setParents(Collections.singletonList(folderId));

		if (folderId != null && !folderId.isEmpty()) { 
			System.out.println(folderId);

		}
		FileContent mediaContent = new FileContent("image/jpeg", ref);

		File uploadedFile = serviceDrive.files().create(fileMetadata, mediaContent).setFields("id").execute();
		System.out.println("File ID: " + uploadedFile.getId());

	}
}
