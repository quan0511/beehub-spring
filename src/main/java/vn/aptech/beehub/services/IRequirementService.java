package vn.aptech.beehub.services;

import java.util.Map;

import vn.aptech.beehub.dto.RequirementDto;

public interface IRequirementService {
	public Map<String, String> handleRequirement(Long id, RequirementDto requirement);
}
