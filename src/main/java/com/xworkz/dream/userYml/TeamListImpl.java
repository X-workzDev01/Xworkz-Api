package com.xworkz.dream.userYml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.xworkz.dream.dto.utils.Team;

@Service
public class TeamListImpl implements TeamList {

	@Value("${login.teamFile}")
	private String userFile;
	private String loginEmail;

	@Autowired
	private ResourceLoader resourceLoader;

	public List<Team> getTeam() throws IOException {

		List<Team> teams = new ArrayList<Team>();

		Yaml yaml = new Yaml();
		Resource resource = resourceLoader.getResource(userFile);
		File file = resource.getFile();
		FileInputStream inputStream = new FileInputStream(file);
		Map<String, Map<Object, Object>> yamlData = (Map<String, Map<Object, Object>>) yaml.load(inputStream);
		List<Object> list = (List<Object>) yamlData.get("team");
		ObjectMapper objectMapper = new ObjectMapper();

		for (Object object : list) {
			Team user = objectMapper.convertValue(object, Team.class);
			teams.add(user);
		}
		return teams;
	}

}
