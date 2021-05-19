import { configureStore } from "@reduxjs/toolkit";
import { projectApi } from "../../../apis/projects/project";

export default configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectApi.middleware),
});
