import React from "react";
import { getCart } from "../../apis/cart/cart";

const SamplesStateContext = React.createContext();
const SamplesDispatchContext = React.createContext();

function reducer(state, action) {
  switch (action.type) {
    case "loaded":
      return { ...state };
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
}

function SamplesProvider({ children }) {
  const [state, dispatch] = React.useReducer(reducer, {});

  React.useEffect(() => {
    getCart().then((result) => console.log(result));
  }, []);

  return (
    <SamplesStateContext.Provider value={state}>
      <SamplesDispatchContext.Provider value={dispatch}>
        {children}
      </SamplesDispatchContext.Provider>
    </SamplesStateContext.Provider>
  );
}

function useSamplesState() {
  const context = React.useContext(SamplesStateContext);
  if (context === undefined) {
    throw new Error(`useSamplesState must be used with a SamplesProvider`);
  }
  return context;
}

function useSamplesDispatch() {
  const context = React.useContext(SamplesDispatchContext);
  if (context === undefined) {
    throw new Error(`useSamplesDispatch must be used with a SamplesProvider`);
  }
  return context;
}

export { SamplesProvider, useSamplesState, useSamplesDispatch };
