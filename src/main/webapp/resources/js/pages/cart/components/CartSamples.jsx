import { Button, Input } from "antd";
import React from "react";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";
import styled from "styled-components";
import {
  useCountQuery,
  useEmptyMutation,
  useGetCartQuery,
} from "../../../apis/cart/cart";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { blue6, grey1, grey3, red4, red6 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { SampleRenderer } from "./SampleRenderer";

const { Search } = Input;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 400px;
`;

const CartTools = styled.div`
  padding: 0 ${SPACE_SM};
  height: 65px;
  border-bottom: ${BORDERED_LIGHT};
  display: flex;
  align-items: center;

  .ant-input {
    background-color: ${grey1};

    &:hover {
      background-color: ${grey3};
    }

    &:focus {
      border: 1px solid ${blue6};
      background-color: ${grey1};
    }
  }
`;

const CartSamplesWrapper = styled.div`
  flex-grow: 1;
`;

const ButtonsPanelBottom = styled.div`
  height: 60px;
  padding: ${SPACE_SM};
  border-top: ${BORDERED_LIGHT};
  display: flex;
  justify-content: center;
  align-items: center;
`;

const EmptyCartButton = styled(Button)`
  background-color: ${red4};
  color: ${grey1};

  &:hover {
    background-color: ${red6};
    color: ${grey1};
  }
`;

export default function CartSamples({
  applyFilter,
  displaySample,
  removeSample,
  removeProject,
}) {
  const { data: count = 0, refetch: refetchCount } = useCountQuery();
  const { data: samples, isSuccess, isFetching, refetch } = useGetCartQuery(
    undefined,
    {
      skip: !count || count === 0,
    }
  );
  const [emptyCart] = useEmptyMutation();

  const filterSamples = (e) => applyFilter(e.target.value);

  const removeOneProject = (id) => removeProject(id);

  const renderSample = ({ index, data, style }) => {
    const sample = samples[index];
    return (
      <SampleRenderer
        rowIndex={index}
        data={sample}
        style={style}
        displaySample={displaySample}
        removeSample={() => removeSample(sample.project.id, sample.id)}
        removeProject={removeOneProject}
      />
    );
  };

  const empty = () => emptyCart().then(refetchCount);

  return (
    <Wrapper>
      {isFetching ? (
        <p>LOADING</p>
      ) : (
        <>
          <CartTools>
            <Search onChange={filterSamples} />
          </CartTools>
          <CartSamplesWrapper className="t-samples-list">
            <AutoSizer>
              {({ height = 600, width = 400 }) => (
                <VList
                  itemCount={samples.length}
                  itemSize={75}
                  height={height}
                  width={width}
                >
                  {renderSample}
                </VList>
              )}
            </AutoSizer>
          </CartSamplesWrapper>
          <ButtonsPanelBottom>
            <EmptyCartButton
              className="t-empty-cart-btn"
              type="danger"
              block
              onClick={empty}
            >
              {i18n("cart.clear")}
            </EmptyCartButton>
          </ButtonsPanelBottom>
        </>
      )}
    </Wrapper>
  );
}
