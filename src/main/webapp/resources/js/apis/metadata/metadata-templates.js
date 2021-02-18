import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata-templates`);

/**
 * Get all metadata templates associated with a project
 * @param projectId
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getProjectMetadataTemplates(projectId) {
  return axios
    .get(`${BASE_URL}?projectId=${projectId}`)
    .then(({ data }) => data);
}

export async function createProjectMetadataTemplate(projectId, parameters) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}?projectId=${projectId}`,
      parameters
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

export async function getMetadataTemplate(id) {
  try {
    const { data } = await axios.get(`${BASE_URL}/${id}`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
