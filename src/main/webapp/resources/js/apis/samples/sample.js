import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

export const sampleAPI = createApi({
  reducerPath: "sampleAPI",
  baseQuery: fetchBaseQuery({ baseUrl: setBaseUrl("ajax/samples") }),
  tagTypes: ["Sample"],
  endpoints: (build) => ({
    getSampleDetails: build.query({
      query: (sampleId) => ({
        url: "/details",
        params: { sampleId },
        providesTags: ["Sample"],
      }),
    }),
  }),
});

export const { useGetSampleDetailsQuery } = sampleAPI;
