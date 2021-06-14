import { configureStore } from "@reduxjs/toolkit";
import { sampleAPI } from "../../apis/samples/sample";

export default configureStore({
  reducer: {
    [sampleAPI.reducerPath]: sampleAPI.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(sampleAPI.middleware),
});
