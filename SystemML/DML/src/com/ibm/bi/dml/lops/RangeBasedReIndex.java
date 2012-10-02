package com.ibm.bi.dml.lops;

import com.ibm.bi.dml.lops.LopProperties.ExecLocation;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.lops.compile.JobType;
import com.ibm.bi.dml.parser.Expression.DataType;
import com.ibm.bi.dml.parser.Expression.ValueType;
import com.ibm.bi.dml.utils.LopsException;


public class RangeBasedReIndex extends Lops {

	/**
	 * Constructor to setup a RangeBasedReIndex operation.
	 * 
	 * @param input
	 * @param op
	 * @return 
	 * @throws LopsException
	 */
	
	private boolean forLeftIndexing=false;

	private void init(Lops inputMatrix, Lops rowL, Lops rowU, Lops colL, Lops colU, Lops leftMatrixRowDim, 
			Lops leftMatrixColDim, DataType dt, ValueType vt, ExecType et, boolean forleft) {
		
		this.addInput(inputMatrix);
		this.addInput(rowL);
		this.addInput(rowU);
		this.addInput(colL);
		this.addInput(colU);
		this.addInput(leftMatrixRowDim);
		this.addInput(leftMatrixColDim);
		
		inputMatrix.addOutput(this);		
		rowL.addOutput(this);
		rowU.addOutput(this);
		colL.addOutput(this);
		colU.addOutput(this);
		leftMatrixRowDim.addOutput(this);
		leftMatrixColDim.addOutput(this);

		boolean breaksAlignment = true;
		boolean aligner = false;
		boolean definesMRJob = false;
		
		if ( et == ExecType.MR ) {
			
			lps.addCompatibility(JobType.GMR);
			lps.addCompatibility(JobType.RAND);
			lps.addCompatibility(JobType.MMCJ);
			lps.addCompatibility(JobType.MMRJ);
			this.lps.setProperties(et, ExecLocation.Map, breaksAlignment, aligner, definesMRJob);
		} 
		else {
			lps.addCompatibility(JobType.INVALID);
			this.lps.setProperties(et, ExecLocation.ControlProgram, breaksAlignment, aligner, definesMRJob);
		}
		
		forLeftIndexing=forleft;
	}
	
	public RangeBasedReIndex(
			Lops input, Lops rowL, Lops rowU, Lops colL, Lops colU, Lops rowDim, Lops colDim, DataType dt, ValueType vt, boolean forleft)
			throws LopsException {
		super(Lops.Type.RangeReIndex, dt, vt);
		init(input, rowL, rowU, colL, colU,  rowDim, colDim, dt, vt, ExecType.MR, forleft);
	}

	public RangeBasedReIndex(
			Lops input, Lops rowL, Lops rowU, Lops colL, Lops colU, Lops rowDim, Lops colDim, DataType dt, ValueType vt, ExecType et, boolean forleft)
			throws LopsException {
		super(Lops.Type.RangeReIndex, dt, vt);
		init(input, rowL, rowU, colL, colU, rowDim, colDim, dt, vt, et, forleft);
	}
	
	public RangeBasedReIndex(
			Lops input, Lops rowL, Lops rowU, Lops colL, Lops colU, Lops rowDim, Lops colDim, DataType dt, ValueType vt)
			throws LopsException {
		super(Lops.Type.RangeReIndex, dt, vt);
		init(input, rowL, rowU, colL, colU,  rowDim, colDim, dt, vt, ExecType.MR, false);
	}

	public RangeBasedReIndex(
			Lops input, Lops rowL, Lops rowU, Lops colL, Lops colU, Lops rowDim, Lops colDim, DataType dt, ValueType vt, ExecType et)
			throws LopsException {
		super(Lops.Type.RangeReIndex, dt, vt);
		init(input, rowL, rowU, colL, colU, rowDim, colDim, dt, vt, et, false);
	}
	
	private String getOpcode() {
		if(forLeftIndexing)
			return "rangeReIndexForLeft";
		else
			return "rangeReIndex";
	}
	
	@Override
	public String getInstructions(String input, String rowl, String rowu, String coll, String colu, String leftRowDim, String leftColDim, String output) 
	throws LopsException {
		String opcode = getOpcode(); 
		String inst = getExecType() + OPERAND_DELIMITOR + opcode + OPERAND_DELIMITOR + 
		        input + DATATYPE_PREFIX + getInputs().get(0).get_dataType() + VALUETYPE_PREFIX + getInputs().get(0).get_valueType() + OPERAND_DELIMITOR + 
		        rowl + DATATYPE_PREFIX + getInputs().get(1).get_dataType() + VALUETYPE_PREFIX + getInputs().get(1).get_valueType() + OPERAND_DELIMITOR + 
		        rowu + DATATYPE_PREFIX + getInputs().get(2).get_dataType() + VALUETYPE_PREFIX + getInputs().get(2).get_valueType() + OPERAND_DELIMITOR + 
		        coll + DATATYPE_PREFIX + getInputs().get(3).get_dataType() + VALUETYPE_PREFIX + getInputs().get(3).get_valueType() + OPERAND_DELIMITOR + 
		        colu + DATATYPE_PREFIX + getInputs().get(4).get_dataType() + VALUETYPE_PREFIX + getInputs().get(4).get_valueType() + OPERAND_DELIMITOR + 
		        output + DATATYPE_PREFIX + get_dataType() + VALUETYPE_PREFIX + get_valueType();
		
		if(getExecType() == ExecType.MR) {
			// following fields are added only when this lop is executed in MR (both for left & right indexing) 
			inst += OPERAND_DELIMITOR +
			leftRowDim + DATATYPE_PREFIX + getInputs().get(5).get_dataType() + VALUETYPE_PREFIX + getInputs().get(5).get_valueType() + OPERAND_DELIMITOR+
			leftColDim + DATATYPE_PREFIX + getInputs().get(6).get_dataType() + VALUETYPE_PREFIX + getInputs().get(6).get_valueType();
		}
		return inst;
	}

	@Override
	public String getInstructions(int input_index1, int input_index2, int input_index3, int input_index4, int input_index5, int input_index6, int input_index7, int output_index)
			throws LopsException {
		/*
		 * Example: B = A[row_l:row_u, col_l:col_u]
		 * A - input matrix (input_index1)
		 * row_l - lower bound in row dimension
		 * row_u - upper bound in row dimension
		 * col_l - lower bound in column dimension
		 * col_u - upper bound in column dimension
		 * 
		 * Since row_l,row_u,col_l,col_u are scalars, values for input_index(2,3,4,5,6,7) 
		 * will be equal to -1. They should be ignored and the scalar value labels must
		 * be derived from input lops.
		 */
		String rowl = this.getInputs().get(1).getOutputParameters().getLabel();
		if (this.getInputs().get(1).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(1)).isLiteral())
			rowl = "##" + rowl + "##";
		String rowu = this.getInputs().get(2).getOutputParameters().getLabel();
		if (this.getInputs().get(2).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(2)).isLiteral())
			rowu = "##" + rowu + "##";
		String coll = this.getInputs().get(3).getOutputParameters().getLabel();
		if (this.getInputs().get(3).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(3)).isLiteral())
			coll = "##" + coll + "##";
		String colu = this.getInputs().get(4).getOutputParameters().getLabel();
		if (this.getInputs().get(4).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(4)).isLiteral())
			colu = "##" + colu + "##";
		
		String left_nrow = this.getInputs().get(5).getOutputParameters().getLabel();
		if (this.getInputs().get(5).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(5)).isLiteral())
			left_nrow = "##" + left_nrow + "##";
		
		String left_ncol = this.getInputs().get(6).getOutputParameters().getLabel();
		if (this.getInputs().get(6).getExecLocation() != ExecLocation.Data
				|| !((Data) this.getInputs().get(6)).isLiteral())
			left_ncol = "##" + left_ncol + "##";
		
		return getInstructions(Integer.toString(input_index1), rowl, rowu, coll, colu, left_nrow, left_ncol, Integer.toString(output_index));
	}

	@Override
	public String toString() {
		if(forLeftIndexing)
			return "rangeReIndexForLeft";
		else
			return "rangeReIndex";
	}

}
